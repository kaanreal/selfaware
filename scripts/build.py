#!/usr/bin/env python3
"""Build the selected targets and check each distributable jar."""
import argparse
import json
from pathlib import Path
import subprocess
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]


def verify_jar(target):
    version = next(line.split('=', 1)[1] for line in (ROOT / 'gradle.properties').read_text().splitlines()
                   if line.startswith('mod_version='))
    jar = ROOT / 'build' / target['id'] / 'libs' / f"selfaware-{target['id']}-{version}.jar"
    with zipfile.ZipFile(jar) as archive:
        names = archive.namelist()
        mixin = json.loads(archive.read('selfaware.mixins.json'))
        assert mixin['required'] and mixin['injectors']['defaultRequire'] == 1
        assert mixin['client'] == ['LivingEntityRendererMixin'] and not mixin.get('mixins')
        for name in ['NameTagVisibility', 'SelfNameTag']:
            assert f'dev/kaan/selfaware/{name}.class' in names
        assert 'dev/kaan/selfaware/mixin/LivingEntityRendererMixin.class' in names
        assert not any(name.endswith('Test.class') for name in names)
        if 'refmap' in mixin:
            assert json.loads(archive.read(mixin['refmap']))['mappings']
        if target['loader'] == 'quilt':
            metadata = json.loads(archive.read('quilt.mod.json'))
            assert metadata['minecraft']['environment'] == 'client'
            assert metadata['quilt_loader']['version'] == version
            assert {'id': 'minecraft', 'versions': '=' + target['minecraft']} in metadata['quilt_loader']['depends']
            assert 'fabric.mod.json' not in names
        elif target['loader'] == 'fabric':
            metadata = json.loads(archive.read('fabric.mod.json'))
            assert metadata['environment'] == 'client'
            assert metadata['depends']['minecraft'] == '=' + target['minecraft']
            assert metadata['version'] == version and metadata['id'] == 'selfaware'
            assert not any(name.endswith('.toml') for name in names)
        else:
            filename = 'META-INF/mods.toml' if target['loader'] == 'forge' or target['minecraft'] in ['1.20.2', '1.20.4'] else 'META-INF/neoforge.mods.toml'
            metadata = archive.read(filename).decode()
            assert '${' not in metadata and f'versionRange="[{target["minecraft"]}]"' in metadata
            assert 'fabric.mod.json' not in names
        for name in names:
            if name.endswith('.class'):
                major = int.from_bytes(archive.read(name)[6:8], 'big')
                assert major <= target['java'] + 44, (name, major)
    return str(jar.relative_to(ROOT))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('targets', nargs='*', help='Target ids from versions.json; defaults to all')
    parser.add_argument('--loader', choices=['fabric', 'forge', 'neoforge', 'quilt'])
    parser.add_argument('--list', action='store_true')
    parser.add_argument('--verify-only', action='store_true')
    args = parser.parse_args()
    targets = json.loads((ROOT / 'versions.json').read_text())
    ids = [target['id'] for target in targets]
    assert len(ids) == len(set(ids)), 'Duplicate target ids'
    unknown = set(args.targets) - set(ids)
    if unknown:
        parser.error('Unknown targets: ' + ', '.join(sorted(unknown)))
    if args.targets:
        targets = [target for target in targets if target['id'] in args.targets]
    if args.loader:
        targets = [target for target in targets if target['loader'] == args.loader]
    if args.list:
        print(json.dumps([target['id'] for target in targets]))
        return 0
    results = []
    log_dir = ROOT / 'build' / 'verification'
    log_dir.mkdir(parents=True, exist_ok=True)
    for target in targets:
        identifier = target['id']
        print(f'Building {identifier}', flush=True)
        log = log_dir / (identifier + '.log')
        try:
            if not args.verify_only:
                command = [str(ROOT / 'gradlew'), 'build', f'-Ptarget={identifier}', '--console=plain']
                if target['loader'] == 'neoforge' and target['minecraft'].startswith('26.'):
                    # NeoGradle adds an empty game-test task that downloads runtime assets and can hang in CI.
                    command += ['-x', 'testJunit']
                with log.open('w') as output:
                    subprocess.run(command, cwd=ROOT, stdout=output, stderr=subprocess.STDOUT,
                                   check=True, timeout=1200)
            artifact = verify_jar(target)
            results.append({'target': identifier, 'build': 'passed', 'artifact': artifact})
            print(f'Passed {identifier}', flush=True)
        except (subprocess.SubprocessError, AssertionError, OSError, KeyError, ValueError) as error:
            results.append({'target': identifier, 'build': 'failed', 'error': str(error), 'log': str(log.relative_to(ROOT))})
            print(f'Failed {identifier}: {error}; see {log.relative_to(ROOT)}', flush=True)
        (log_dir / 'results.json').write_text(json.dumps(results, indent=2) + '\n')
    return int(any(result['build'] != 'passed' for result in results))


if __name__ == '__main__':
    sys.exit(main())
