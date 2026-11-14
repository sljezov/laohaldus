"""Salvestab Java protsessi päris PTY-väljundi asciicast- ja HTML-kujul (macOS/Linux)."""
import errno
import html
import json
import os
from pathlib import Path
import pty
import select
import subprocess
import time

ROOT = Path(__file__).resolve().parents[1]
subprocess.run(["javac", "-encoding", "UTF-8", "--release", "17", "-d", "build",
                *map(str, (ROOT / "src/ee/laohaldus").glob("*.java"))], cwd=ROOT, check=True)
master, slave = pty.openpty()
process = subprocess.Popen(["java", "-cp", "build", "ee.laohaldus.Main"],
                           cwd=ROOT, stdin=slave, stdout=slave, stderr=slave)
os.close(slave)
start = time.monotonic()
events = []


def collect(seconds):
    deadline = time.monotonic() + seconds
    while time.monotonic() < deadline:
        ready, _, _ = select.select([master], [], [], max(0, deadline - time.monotonic()))
        if ready:
            try:
                data = os.read(master, 65536)
            except OSError as error:
                if error.errno == errno.EIO:
                    return
                raise
            if not data:
                return
            events.append([round(time.monotonic() - start, 3), "o", data.decode("utf-8")])


collect(0.7)
for line in ["1", "2", "2", "Klaviatuur", "25,50", "2", "24", "4", "klav", "5",
             "3", "Klaviatuur", "-1", "5", "0"]:
    os.write(master, (line + "\n").encode("utf-8"))
    collect(0.35)
collect(0.2)
process.wait(timeout=5)
os.close(master)
assert process.returncode == 0
transcript = "".join(event[2] for event in events)
for expected in ["Toode lisatud: Klaviatuur", "Lao koguväärtus: 140.00 EUR",
                 "Lao koguväärtus: 114.50 EUR", "Head aega!"]:
    assert expected in transcript, expected
header = {"version": 2, "width": 110, "height": 30, "timestamp": int(time.time()),
          "env": {"TERM": "xterm-256color", "SHELL": "/bin/sh"}, "title": "Laohalduse demo"}
(ROOT / "docs/kaivitustoend.cast").write_text(
    "\n".join(json.dumps(item, ensure_ascii=False) for item in [header, *events]) + "\n",
    encoding="utf-8")
(ROOT / "docs/kaivitustoend.txt").write_text(transcript.replace("\r\n", "\n"), encoding="utf-8")
page = '''<!doctype html>
<html lang="et"><meta charset="utf-8"><meta name="viewport" content="width=device-width">
<title>Laohalduse käivitustõend</title>
<style>body{max-width:1000px;margin:32px auto;padding:0 20px;background:#101827;color:#e8edf7;
font:17px system-ui}button{padding:10px 18px;margin:0 8px 12px 0;cursor:pointer}
pre{background:#050b14;padding:20px;overflow:auto;font:15px/1.55 monospace;border-radius:8px}
summary{cursor:pointer}</style>
<h1>Laohalduse käivitustõend</h1>
<p>Päris Java protsessi terminalisalvestus: kolm algtoodet, toote lisamine,
otsing, koguväärtus ja koguse vähendamine. Salvestus kestab umbes kuus sekundit.</p>
<button id="play">Esita algusest</button><button id="stop">Peata</button>
<span id="status" role="status">Valmis</span><pre id="terminal" aria-label="Terminalisalvestus"></pre>
<details><summary>Kogu terminaliväljund</summary><pre>TRANSCRIPT</pre></details>
<script>
const events=EVENTS; let timers=[];
const terminal=document.getElementById('terminal'),status=document.getElementById('status');
function stop(){timers.forEach(clearTimeout);timers=[];}
document.getElementById('stop').onclick=()=>{stop();status.textContent='Peatatud';};
document.getElementById('play').onclick=()=>{stop();terminal.textContent='';status.textContent='Esitamine';
events.forEach((event,index)=>timers.push(setTimeout(()=>{
terminal.textContent+=event[2].replaceAll('\\r',''); terminal.scrollTop=terminal.scrollHeight;
if(index===events.length-1)status.textContent='Lõppenud';},event[0]*1000)));};
</script></html>'''
page = page.replace("TRANSCRIPT", html.escape(transcript)).replace(
    "EVENTS", json.dumps(events, ensure_ascii=False).replace("<", "\\u003c"))
(ROOT / "docs/kaivitustoend.html").write_text(page, encoding="utf-8")
print("Salvestatud päris terminaliseanss: .cast, .html ja .txt")
