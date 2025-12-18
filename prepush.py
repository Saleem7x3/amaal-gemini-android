#!/usr/bin/env python3
"""
Android Pre-Push Normalizer (CI-first, Termux-safe)

This script:
- NEVER runs Gradle
- NEVER evaluates build logic
- ONLY prepares files so GitHub Actions can build safely
"""

import os
import sys
import shutil
import zipfile
import urllib.request
from pathlib import Path
from datetime import datetime

ROOT = Path.cwd()
BACKUP_DIR = ROOT / ".prepush_backup"
GRADLE_VERSION = "8.2.1"

def die(msg):
    print(f"\n❌ {msg}")
    sys.exit(1)

def info(msg):
    print(f"▶ {msg}")

def ok(msg):
    print(f"✅ {msg}")

print("\n=== ANDROID PRE-PUSH SANITY & CI NORMALIZATION ===\n")

# --------------------------------------------------
# 0. Basic sanity
# --------------------------------------------------
required = ["app", "settings.gradle", "build.gradle"]
for r in required:
    if not (ROOT / r).exists():
        die(f"Missing required project item: {r}")

ok("Project structure detected")

# --------------------------------------------------
# 1. Backup originals (once)
# --------------------------------------------------
if not BACKUP_DIR.exists():
    BACKUP_DIR.mkdir()
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")

    for f in ["settings.gradle", "build.gradle", "gradle.properties"]:
        p = ROOT / f
        if p.exists():
            shutil.copy(p, BACKUP_DIR / f"{f}.{timestamp}.bak")

    ok("Original files backed up")
else:
    info("Backup already exists — skipping")

# --------------------------------------------------
# 2. Normalize settings.gradle (ONLY repo authority)
# --------------------------------------------------
(ROOT / "settings.gradle").write_text(f"""
pluginManagement {{
    repositories {{
        google()
        mavenCentral()
        gradlePluginPortal()
    }}
    plugins {{
        id "com.android.application" version "8.1.2" apply false
    }}
}}

dependencyResolutionManagement {{
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {{
        google()
        mavenCentral()
    }}
}}

rootProject.name = "Amaal"
include(":app")
""".strip() + "\n")

ok("settings.gradle normalized")

# --------------------------------------------------
# 3. Sanitize root build.gradle (NO repositories)
# --------------------------------------------------
(ROOT / "build.gradle").write_text("""
tasks.register("clean", Delete) {
    delete rootProject.buildDir
}
""".strip() + "\n")

ok("build.gradle sanitized")

# --------------------------------------------------
# 4. Enforce gradle.properties (AndroidX safety)
# --------------------------------------------------
gp = ROOT / "gradle.properties"
props = {}

if gp.exists():
    for line in gp.read_text().splitlines():
        if "=" in line:
            k, v = line.split("=", 1)
            props[k.strip()] = v.strip()

props["android.useAndroidX"] = "true"
props["android.enableJetifier"] = "true"
props["org.gradle.jvmargs"] = "-Xmx2048m"

gp.write_text("\n".join(f"{k}={v}" for k, v in props.items()) + "\n")
ok("gradle.properties enforced")

# --------------------------------------------------
# 5. Ensure Gradle Wrapper (manual install, no Gradle)
# --------------------------------------------------
wrapper_dir = ROOT / "gradle" / "wrapper"
wrapper_jar = wrapper_dir / "gradle-wrapper.jar"
wrapper_props = wrapper_dir / "gradle-wrapper.properties"
gradlew = ROOT / "gradlew"

wrapper_dir.mkdir(parents=True, exist_ok=True)

if not wrapper_props.exists():
    wrapper_props.write_text(f"""
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-{GRADLE_VERSION}-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
""".strip() + "\n")

if not wrapper_jar.exists():
    info(f"Downloading Gradle {GRADLE_VERSION} wrapper")

    zip_path = ROOT / f"gradle-{GRADLE_VERSION}.zip"
    url = f"https://services.gradle.org/distributions/gradle-{GRADLE_VERSION}-bin.zip"

    urllib.request.urlretrieve(url, zip_path)

    with zipfile.ZipFile(zip_path, "r") as z:
        jar_paths = [n for n in z.namelist() if n.endswith("gradle-wrapper.jar")]
        if not jar_paths:
            die("gradle-wrapper.jar not found in Gradle distribution")

        jar_src = jar_paths[0]
        z.extract(jar_src, wrapper_dir)
        extracted = wrapper_dir / jar_src
        extracted.rename(wrapper_jar)

        shutil.rmtree(wrapper_dir / jar_src.split("/")[0], ignore_errors=True)

    zip_path.unlink()
    ok("gradle-wrapper.jar installed")
else:
    ok("gradle-wrapper.jar already present")

# --------------------------------------------------
# 6. Ensure gradlew executable
# --------------------------------------------------
if not gradlew.exists():
    die("gradlew script missing")

gradlew.chmod(0o755)
ok("gradlew executable")

# --------------------------------------------------
# 7. Final summary
# --------------------------------------------------
print("\n=== RESULT ===")
ok("Project is CI-safe")
ok("No local Gradle dependency")
ok("Ready for GitHub Actions build")

print("\n👉 NEXT: commit & push, then build ONLY via GitHub Actions\n")
