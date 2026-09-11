"""Resolve released Simple Voice Chat files from Modrinth."""
import json
import urllib.parse
import urllib.request


API = "https://api.modrinth.com/v2/project/simple-voice-chat/version"
_CACHE = {}


def latest(loader, minecraft):
    key = (loader, minecraft)
    if key in _CACHE:
        return _CACHE[key]

    query = urllib.parse.urlencode({
        "loaders": json.dumps([loader]),
        "game_versions": json.dumps([minecraft]),
    })
    request = urllib.request.Request(
        f"{API}?{query}",
        headers={"User-Agent": "selfaware-dev/0.1.0"},
    )
    with urllib.request.urlopen(request, timeout=30) as response:
        versions = json.load(response)

    candidates = []
    for version in versions:
        if version.get("version_type") != "release":
            continue
        files = [file for file in version.get("files", []) if file.get("filename", "").endswith(".jar")]
        primary = next((file for file in files if file.get("primary")), files[0] if files else None)
        if primary is None:
            continue
        version_number = version.get("version_number", "").lower()
        filename = primary["filename"].lower()
        if not version_number.startswith(f"{loader}-") and not filename.startswith(f"voicechat-{loader}-"):
            continue
        candidates.append((version.get("date_published", ""), version, primary))

    if not candidates:
        _CACHE[key] = None
        return None

    _, version, primary = max(candidates, key=lambda item: item[0])
    result = {
        "id": version["id"],
        "version": version["version_number"],
        "filename": primary["filename"],
        "url": primary["url"],
    }
    _CACHE[key] = result
    return result
