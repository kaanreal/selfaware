#!/usr/bin/env python3
"""Publish every built target to the Selfaware Modrinth project."""
import argparse
import hashlib
import json
import mimetypes
import os
from pathlib import Path
import sys
import urllib.error
import urllib.parse
import urllib.request
import uuid


ROOT = Path(__file__).resolve().parents[1]
API = "https://api.modrinth.com/v2"
PROJECT_SLUG = "selfaware"
USER_AGENT = "kaanreal/selfaware (https://github.com/kaanreal/selfaware)"


class ApiError(RuntimeError):
    def __init__(self, status, body):
        super().__init__(f"Modrinth returned HTTP {status}: {body}")
        self.status = status


def request(token, method, path, payload=None, content_type="application/json"):
    data = None
    if payload is not None:
        data = json.dumps(payload).encode() if content_type == "application/json" else payload
    headers = {"User-Agent": USER_AGENT}
    if token:
        headers["Authorization"] = token
    if data is not None:
        headers["Content-Type"] = content_type
    req = urllib.request.Request(API + path, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as response:
            body = response.read()
            return json.loads(body) if body else None
    except urllib.error.HTTPError as error:
        body = error.read().decode(errors="replace")
        raise ApiError(error.code, body) from error


def multipart(parts):
    boundary = "selfaware-" + uuid.uuid4().hex
    chunks = []
    for name, value, filename in parts:
        chunks.append(f"--{boundary}\r\n".encode())
        disposition = f'Content-Disposition: form-data; name="{name}"'
        if filename is not None:
            disposition += f'; filename="{filename}"'
        chunks.append((disposition + "\r\n").encode())
        if filename is not None:
            content_type = mimetypes.guess_type(filename)[0] or "application/octet-stream"
            chunks.append(f"Content-Type: {content_type}\r\n".encode())
        chunks.append(b"\r\n")
        chunks.append(value)
        chunks.append(b"\r\n")
    chunks.append(f"--{boundary}--\r\n".encode())
    return b"".join(chunks), f"multipart/form-data; boundary={boundary}"


def create_project(token):
    body = (ROOT / "README.md").read_text(encoding="utf-8")
    data = {
        "slug": PROJECT_SLUG,
        "title": "Selfaware",
        "description": "See your own nametag in third person.",
        "body": body,
        "categories": ["social", "utility"],
        "additional_categories": [],
        "status": "draft",
        "requested_status": None,
        "issues_url": "https://github.com/kaanreal/selfaware/issues",
        "source_url": "https://github.com/kaanreal/selfaware",
        "wiki_url": None,
        "discord_url": None,
        "donation_urls": [],
        "client_side": "required",
        "server_side": "unsupported",
        "environment": "client_only",
        "license_id": "ARR",
        "license_url": None,
        "project_type": "mod",
        "initial_versions": [],
        "is_draft": True,
        "gallery_items": [],
    }
    payload, content_type = multipart([
        ("data", json.dumps(data).encode(), None),
    ])
    return request(token, "POST", "/project", payload, content_type)


def get_or_create_project(token):
    try:
        return request(token, "GET", "/project/" + PROJECT_SLUG)
    except ApiError as error:
        if error.status != 404:
            raise
    print("Creating draft Modrinth project", flush=True)
    return create_project(token)


def dependencies_for(target, dependency_ids):
    dependencies = [{"project_id": dependency_ids["simple-voice-chat"], "dependency_type": "optional"}]
    if target["loader"] in ("fabric", "quilt"):
        dependencies.append({"project_id": dependency_ids["modmenu"], "dependency_type": "required"})
        if not target["minecraft"].startswith("26."):
            dependencies.append({"project_id": dependency_ids["fabric-api"], "dependency_type": "required"})
    return dependencies


def find_jar(artifacts, target, version):
    name = f"selfaware-{target['id']}-{version}.jar"
    matches = list(artifacts.rglob(name))
    if len(matches) != 1:
        raise RuntimeError(f"Expected one {name}, found {len(matches)}")
    return matches[0]


def upload_version(token, project, target, version, jar, changelog, existing, dependency_ids):
    number = f"{version}+mc{target['minecraft']}-{target['loader']}"
    current = existing.get(number)
    sha512 = hashlib.sha512(jar.read_bytes()).hexdigest()
    if current is not None:
        hashes = [file["hashes"]["sha512"] for file in current["files"]]
        if sha512 in hashes:
            print(f"Already published {target['id']}", flush=True)
            return
        raise RuntimeError(f"Modrinth version {number} exists with a different file")

    loader_name = "NeoForge" if target["loader"] == "neoforge" else target["loader"].title()
    data = {
        "name": f"Selfaware {version} for Minecraft {target['minecraft']} ({loader_name})",
        "version_number": number,
        "changelog": changelog,
        "dependencies": dependencies_for(target, dependency_ids),
        "game_versions": [target["minecraft"]],
        "version_type": "release",
        "loaders": [target["loader"]],
        "featured": False,
        "status": "listed",
        "requested_status": "listed",
        "project_id": project["id"],
        "file_parts": ["file"],
        "primary_file": "file",
        "environment": "client_only",
    }
    payload, content_type = multipart([
        ("data", json.dumps(data).encode(), None),
        ("file", jar.read_bytes(), jar.name),
    ])
    request(token, "POST", "/version", payload, content_type)
    print(f"Published {target['id']}", flush=True)


def normalized_version(value):
    return value[1:] if value.startswith("v") else value


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--artifacts", type=Path, required=True)
    parser.add_argument("--version", required=True)
    args = parser.parse_args()
    token = os.environ.get("MODRINTH_TOKEN", "").strip()
    if not token:
        raise RuntimeError("MODRINTH_TOKEN is not configured")
    version = normalized_version(args.version)
    expected = next(line.split("=", 1)[1] for line in (ROOT / "gradle.properties").read_text().splitlines()
                    if line.startswith("mod_version="))
    if version != expected:
        raise RuntimeError(f"Release tag version {version} does not match mod_version {expected}")

    project = get_or_create_project(token)
    versions = request(token, "GET", f"/project/{project['id']}/version")
    existing = {item["version_number"]: item for item in versions}
    targets = json.loads((ROOT / "versions.json").read_text())
    changelog = (ROOT / "RELEASE_NOTES.md").read_text(encoding="utf-8")
    dependency_ids = {
        slug: request(token, "GET", "/project/" + slug)["id"]
        for slug in ("fabric-api", "modmenu", "simple-voice-chat")
    }
    for target in targets:
        jar = find_jar(args.artifacts, target, version)
        upload_version(token, project, target, version, jar, changelog, existing, dependency_ids)

    if project["status"] in ("draft", "private", "unlisted"):
        request(token, "PATCH", f"/project/{project['id']}", {"requested_status": "approved"})
        print("Submitted Modrinth project for review", flush=True)


if __name__ == "__main__":
    try:
        main()
    except (ApiError, OSError, RuntimeError, KeyError, ValueError) as error:
        print(error, file=sys.stderr)
        sys.exit(1)
