#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p build
javac -encoding UTF-8 --release 17 -d build src/ee/laohaldus/*.java
exec java -cp build ee.laohaldus.Main
