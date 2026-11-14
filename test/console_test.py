"""Kontrollib päris konsooliprotsessi väljundeid ja vigadest taastumist."""
import subprocess


def run(lines):
    return subprocess.run(
        ["java", "-cp", "build", "ee.laohaldus.Main"],
        input=lines, text=True, encoding="utf-8", capture_output=True,
        check=True, timeout=10,
    ).stdout


output = run("1\n2\n2\nKlaviatuur\n25,50\n2\n24\n4\nKLAV\n5\n"
             "3\nKlaviatuur\n-1\n5\n3\nKlaviatuur\n-9\n"
             "3\nKlaviatuur\n3\n5\n0\n")
for expected in ["Piim", "Leib", "Hiir", "Toode lisatud: Klaviatuur",
                 "Lao koguväärtus: 140.00 EUR", "Lao koguväärtus: 114.50 EUR",
                 "Viga: Uus kogus", "Lao koguväärtus: 191.00 EUR", "Head aega!"]:
    assert expected in output, expected
assert output.count("Klaviatuur | hind: 25.50 EUR | kogus: 2") == 2
errors = run("x\n2\n9\n2\n1\nÕun\nviga\n2\n1\nÕun\n1.001\n1\n2027-01-01\n"
             "2\n1\nÕun\n1\n1\n2027-02-30\n4\n\n5\n0\n")
assert "Tundmatu valik" in errors
assert "kuni kaks komakohta" in errors
assert "olemasolev kuupäev" in errors
assert "Otsingusõna ei tohi olla tühi" in errors
assert "Lao koguväärtus: 89.00 EUR" in errors
assert "Sisend lõppes" in run("2\n")
print("Kõik konsooli integratsioonitestid läbisid edukalt.")
