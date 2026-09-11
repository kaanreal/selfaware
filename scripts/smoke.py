#!/usr/bin/env python3
"""Run a small runtime smoke matrix and inspect each client log."""
import argparse
from contextlib import nullcontext, contextmanager
import json
import os
from pathlib import Path
import re
import signal
import shutil
import subprocess
import sys
import time
import urllib.request

from gradle_env import for_gradle
from svc_versions import latest as latest_svc


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_TARGETS = [
    "1.20.1-fabric",
    "1.20.1-forge",
    "1.20.1-quilt",
    "1.20.2-neoforge",
    "1.21.1-fabric",
    "1.21.3-fabric",
    "1.21.10-fabric",
    "1.21.11-fabric",
    "26.1-fabric",
    "26.2-fabric",
]
ERROR_PATTERNS = [
    re.compile(r"InvalidInjectionException", re.IGNORECASE),
    re.compile(r"MixinApplyError", re.IGNORECASE),
    re.compile(r"Failed to apply mixin", re.IGNORECASE),
    re.compile(r"NoSuchMethodError", re.IGNORECASE),
    re.compile(r"ClassNotFoundException", re.IGNORECASE),
]
OPTIONAL_RENDER_EVENTS = "de/maxhenkel/voicechat/voice/client/RenderEvents"
OPTIONAL_RENDER_EVENTS_LOOKUP = re.compile(
    rf"Error loading class: {re.escape(OPTIONAL_RENDER_EVENTS)} "
    r"\(java\.lang\.ClassNotFoundException:[^\n]*\)"
)
SELF_AWARE_RENDERER = "net/minecraft/client/renderer/entity/LivingEntityRenderer.class"


def target_data():
    return {target["id"]: target for target in json.loads((ROOT / "versions.json").read_text())}


def has_voicechat(target_id):
    mods_dir = ROOT / "run" / target_id / "mods"
    return any("voicechat" in path.name.lower() for path in mods_dir.glob("*.jar"))


@contextmanager
def prepare_voicechat(target_id, target, svc):
    mods_dir = ROOT / "run" / target_id / "mods"
    hidden = []
    added = None
    try:
        for path in mods_dir.glob("*voicechat*.jar"):
            replacement = path.with_name(path.name + ".selfaware-disabled")
            path.rename(replacement)
            hidden.append((path, replacement))
        if target["loader"] in ["fabric", "quilt"]:
            cache = ROOT / "build" / "svc-cache" / target_id / svc["filename"]
            cache.parent.mkdir(parents=True, exist_ok=True)
            if not cache.exists():
                with urllib.request.urlopen(svc["url"], timeout=60) as response:
                    cache.write_bytes(response.read())
            mods_dir.mkdir(parents=True, exist_ok=True)
            added = mods_dir / svc["filename"]
            shutil.copy2(cache, added)
        yield
    finally:
        if added is not None and added.exists():
            added.unlink()
        for path, replacement in hidden:
            if replacement.exists():
                replacement.rename(path)


def kill_process(process):
    if os.name == "posix":
        try:
            os.killpg(process.pid, signal.SIGTERM)
            process.wait(timeout=5)
            return
        except (ProcessLookupError, subprocess.TimeoutExpired):
            try:
                os.killpg(process.pid, signal.SIGKILL)
            except ProcessLookupError:
                pass
    else:
        process.terminate()
        try:
            process.wait(timeout=5)
        except subprocess.TimeoutExpired:
            process.kill()


def inspect_target(target_id, output, voicechat, started_ns):
    log_path = ROOT / "run" / target_id / "logs" / "latest.log"
    if not log_path.exists() or log_path.stat().st_mtime_ns < started_ns:
        return "no Minecraft log was written"
    text = log_path.read_text(errors="replace")
    renderer_export = ROOT / "run" / target_id / ".mixin.out" / "class" / SELF_AWARE_RENDERER
    loaded_marker = "- selfaware " in text.lower() or "| selfaware " in text.lower()
    loaded_export = renderer_export.is_file() and renderer_export.stat().st_mtime_ns >= started_ns
    if not (loaded_marker or loaded_export):
        return "Selfaware did not load"
    if voicechat and "NoSuchMethodError" in text and "de.maxhenkel.voicechat" in text:
        return "Simple Voice Chat dev jar failed before Selfaware"
    for pattern in ERROR_PATTERNS:
        if pattern.pattern == r"ClassNotFoundException" and not voicechat:
            text = OPTIONAL_RENDER_EVENTS_LOOKUP.sub("", text)
            output = OPTIONAL_RENDER_EVENTS_LOOKUP.sub("", output)
        if pattern.search(text) or pattern.search(output):
            return f"runtime error matched {pattern.pattern}"
    if voicechat and "Registering events for 'selfaware'" not in text:
        return "Simple Voice Chat did not register Selfaware"
    if voicechat:
        exported = ROOT / "run" / target_id / ".mixin.out" / "class"
        render_events = [
            path for path in exported.rglob("de/maxhenkel/voicechat/voice/client/RenderEvents.class")
            if path.stat().st_mtime_ns >= started_ns
        ]
        if not render_events:
            return "Simple Voice Chat RenderEvents was not exported"
    return None


def run_target(target_id, timeout, logs_dir, known_targets, svc=None):
    voicechat = svc is not None or has_voicechat(target_id)
    output_path = logs_dir / f"{target_id}.log"
    command = [str(ROOT / "gradlew"), "runClient", f"-Ptarget={target_id}", "-Psmoke", "--console=plain"]
    target = known_targets[target_id]
    if svc and target["loader"] not in ["fabric", "quilt"]:
        command.append(f"-PsvcVersion={svc['id']}")
    print(f"Smoke {target_id}" + (" with SVC" if voicechat else ""), flush=True)
    if target_id not in known_targets:
        return {"target": target_id, "status": "failed", "error": "unknown target"}
    started_ns = time.time_ns()
    local_voicechat = prepare_voicechat(target_id, target, svc) if svc else nullcontext()
    with local_voicechat:
        with output_path.open("w") as output:
            process = subprocess.Popen(
                command,
                cwd=ROOT,
                stdout=output,
                stderr=subprocess.STDOUT,
                start_new_session=os.name == "posix",
                env=for_gradle(),
            )
            timed_out = False
            try:
                process.wait(timeout=timeout)
            except subprocess.TimeoutExpired:
                timed_out = True
                kill_process(process)
    output = output_path.read_text(errors="replace")
    error = inspect_target(target_id, output, voicechat, started_ns)
    if error is None and not timed_out and process.returncode != 0:
        error = f"Gradle exited with status {process.returncode}"
    if error is None:
        print(f"Passed {target_id}", flush=True)
        return {"target": target_id, "status": "passed", "timed_out": timed_out, "log": str(output_path.relative_to(ROOT))}
    print(f"Failed {target_id}: {error}", flush=True)
    return {"target": target_id, "status": "failed", "error": error, "log": str(output_path.relative_to(ROOT))}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("targets", nargs="*", help="target ids; defaults to one per adapter and loader")
    parser.add_argument("--timeout", type=int, default=120, help="seconds per client, default: 120")
    parser.add_argument("--list", action="store_true", help="print the default smoke targets")
    parser.add_argument("--svc-all", action="store_true", help="run every target with its latest released SVC jar")
    args = parser.parse_args()

    known_targets = target_data()
    targets = args.targets or (list(known_targets) if args.svc_all else DEFAULT_TARGETS)
    unknown = sorted(set(targets) - set(known_targets))
    if unknown:
        parser.error("Unknown targets: " + ", ".join(unknown))
    if args.list:
        print("\n".join(targets))
        return 0

    logs_dir = ROOT / "build" / "smoke"
    logs_dir.mkdir(parents=True, exist_ok=True)
    results = []
    for target_id in targets:
        target = known_targets[target_id]
        try:
            svc = latest_svc(target["loader"], target["minecraft"]) if args.svc_all else None
        except Exception as error:
            print(f"Failed {target_id}: could not resolve Simple Voice Chat: {error}", flush=True)
            results.append({
                "target": target_id,
                "status": "failed",
                "error": f"could not resolve Simple Voice Chat: {error}",
            })
            continue
        if args.svc_all and svc is None:
            print(f"Skip {target_id}: no released Simple Voice Chat jar", flush=True)
            results.append({
                "target": target_id,
                "status": "skipped",
                "reason": "no released Simple Voice Chat jar",
            })
            continue
        result = run_target(target_id, args.timeout, logs_dir, known_targets, svc)
        if svc:
            result["svc"] = svc
        results.append(result)
    result_file = logs_dir / ("svc-results.json" if args.svc_all else "results.json")
    result_file.write_text(json.dumps(results, indent=2) + "\n")
    return int(any(result["status"] == "failed" for result in results))


if __name__ == "__main__":
    sys.exit(main())
