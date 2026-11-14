# Kasutatud Git-commit'id

Kolm sisulist arendusetappi loodi töö käigus, iga etapi järel kontrolliti koodi:

| Commit | Teema | Tulemus |
| --- | --- | --- |
| `382560c` | lisa kapseldatud tooted ja tooteliikide pärilus | Kompileeruv abstraktne toode ja kaks alamklassi |
| `0f97c58` | lisa laohaldus otsingu kogusemuudatuste ja testidega | Toimiv laohaldus ning edukalt läbitud laoloogika testid |
| `722ccff` | lisa konsoolimenüü sisendikontroll ja käivitusskriptid | Töötav konsool ja edukalt läbitud integratsioonitestid |

Neile järgneb eraldi dokumentatsiooni ja käivitustõendi commit pealkirjaga
`lisa eestikeelne dokumentatsioon ja päris terminalisalvestus`.
Selle commit'i räsi saab vaadata bundle'i kloonimise järel `git log --oneline` abil.

Kõik commit'ide teemad on väiketähtedega. Täielik ajalugu on projektiga kaasas
failis `laohaldus.bundle`. Klooni see tavalises kirjutamisõigusega keskkonnas:

```sh
git clone laohaldus.bundle ../laohaldus-git
git -C ../laohaldus-git log --oneline
```
