#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p build
javac -encoding UTF-8 --release 17 -d build src/ee/laohaldus/*.java test/ee/laohaldus/*.java
java -cp build ee.laohaldus.InventoryTest
python3 test/console_test.py
