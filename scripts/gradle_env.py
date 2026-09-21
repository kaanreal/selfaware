"""Small environment helpers shared by the build scripts."""
import os
from pathlib import Path
import shutil
import subprocess
import sys


def for_gradle():
    env = os.environ.copy()
    if env.get("JAVA_HOME"):
        return env

    candidates = [
        Path("/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home"),
        Path("/usr/local/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home"),
    ]
    if os.name == "nt":
        candidates.extend(sorted(
            (Path.home() / ".gradle" / "jdks").glob("*-25-*-windows.*"),
            reverse=True,
        ))
    brew = shutil.which("brew")
    if brew:
        try:
            prefix = subprocess.run(
                [brew, "--prefix", "openjdk@25"],
                check=True,
                capture_output=True,
                text=True,
            ).stdout.strip()
            if prefix:
                candidates.append(Path(prefix) / "libexec/openjdk.jdk/Contents/Home")
        except (OSError, subprocess.SubprocessError):
            pass

    if sys.platform == "darwin":
        try:
            java_home = subprocess.run(
                ["/usr/libexec/java_home", "-v", "25"],
                check=True,
                capture_output=True,
                text=True,
            ).stdout.strip()
            if java_home:
                candidates.append(Path(java_home))
        except (OSError, subprocess.SubprocessError):
            pass

    java_name = "java.exe" if os.name == "nt" else "java"
    for candidate in candidates:
        if (candidate / "bin" / java_name).is_file():
            env["JAVA_HOME"] = str(candidate)
            break
    return env
