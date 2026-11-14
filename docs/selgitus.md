# Lahenduse dokumentatsioon

## Lühike tööpäevik

Kõigepealt valmis abstraktne `Product` ning sellest pärinevad toidu- ja elektroonikaklassid.
Seejärel lisandus `Inventory`, mille lisamist, otsingut, koguste muutmist ja koguväärtust kontrolliti testidega.
Kolmandas etapis valmis konsoolimenüü koos sisendikontrolliga ning testiti kasutaja tegevusi päris Java protsessis.
Kõige rohkem tähelepanu nõudis vigase sisendi käsitlemine nii, et negatiivne laojääk, liiga täpne hind või vale kuupäev ei muudaks laoseisu.
Lõpuks salvestati töötava rakenduse näidisseanss ja koostati dokumentatsioon ning Git-ajaloo bundle.

See päevik kirjeldab siinse lahenduse valmimist. Isiklikul esitlusel tasub selgitused
oma sõnadega läbi rääkida ja rakendust ise käivitada.

## Neli objektorienteeritud põhimõtet

### 1. Kapseldamine

Näide: [Product.java](../src/ee/laohaldus/Product.java), väljad `private BigDecimal price`
ja `private int quantity` ning meetodid `setPrice()` ja `changeQuantity()`.

Toote sisemisi andmeid ei saa teisest klassist otse üle kirjutada. Hinna ja koguse
muutmiseks tuleb kasutada meetodeid, mis kontrollivad väärtust enne salvestamist.
Näiteks `changeQuantity(-9)` ei õnnestu, kui laos on ainult kaheksa ühikut.
See on kasulik, sest sama reegel kehtib nii konsoolist kui ka mõnest tulevasest
kasutajaliidesest tehtud muudatustele. Nimi määratakse konstruktoris ja jääb muutumatuks;
seda pole selle ülesande toiminguteks vaja muuta.

### 2. Pärilus

Näide: [FoodProduct.java](../src/ee/laohaldus/FoodProduct.java),
`public class FoodProduct extends Product`.

Toidukaup on üks toote liik. See saab vanemklassist nime, hinna, koguse ja ühised
meetodid ning lisab säilivuskuupäeva. `ElectronicProduct` lisab samamoodi garantii.
Pärilus võimaldab ühiseid kontrolle kasutada mõlemas klassis ilma sama koodi kopeerimata.

### 3. Polümorfism

Näide: [Product.java](../src/ee/laohaldus/Product.java), `toString()` kutsub
`getAdditionalInfo()` meetodit, mille mõlemad alamklassid üle kirjutavad.
[Main.java](../src/ee/laohaldus/Main.java) läbib tooteid `Product`-tüüpi muutujaga
ja kuvab need käsuga `System.out.println(product)`.

Kuigi muutuja tüüp on `Product`, valitakse lisainfo tegeliku objekti järgi:
toidul kuvatakse „parim enne”, elektroonikal garantii. Kuvamistsükkel ei pea
tooteliigi järgi eraldi tingimusi sisaldama. See lihtsustab uue tooteliigi lisamist.

### 4. Abstraktsioon

Näide: [Product.java](../src/ee/laohaldus/Product.java), `abstract class Product`
ja `public abstract String getAdditionalInfo()`.

Abstraktne klass kirjeldab, mis on kõigil toodetel ühist, kuid jätab tooteliigi
lisainfo alamklassi otsustada. Otse `new Product(...)` objekti luua ei saa.
Laohaldus saab töötada üldise tootega, teadmata säilivuskuupäeva või garantii üksikasju.
Nii jääb ühine laoloogika tooteliigi detailidest sõltumatuks.

## Andmestruktuuri põhjendus

[Inventory.java](../src/ee/laohaldus/Inventory.java) hoiab tooteid väljal
`private final List<Product> products = new ArrayList<>()`.
`ArrayList` on dünaamiline loend, mis sobib väikese poe mõne toote jaoks:
see kasvab uute toodete lisamisel ja säilitab lisamise järjekorra.
Toodete koguarvu ei pea enne programmi käivitamist teadma.

- Lisamine: `addProduct()` kontrollib esmalt samanimelise toote puudumist ja kutsub `products.add(product)`.
- Otsing: `searchByName()` käib loendi läbi, teisendab nimed `Locale.ROOT` abil väiketähtedeks ja kontrollib nimeosa `contains()` abil. Vastetest tekib uus loend.
- Läbimine: `getTotalValue()` liidab iga toote hinna ja koguse korrutise; konsool kuvab tooted `for`-tsüklis.

Otsing, koguse muutmiseks toote leidmine ja koguväärtuse leidmine on O(n), kus n on
toodete arv. Loendisse lisamine ise on amortiseeritult O(1), kuid duplikaadikontrolli
tõttu on kogu `addProduct()` O(n). Väikeses rakenduses on see piisav ja lihtne mõista.
Suures laos võiks täpse nime või tunnuse järgi otsimiseks lisada `HashMap`-i,
kuid nimeosa otsing vajaks endiselt läbimist või eraldi otsinguindeksit.
`getProducts()` tagastab muutmatu loendikoopia; toodete endi kontrollitud meetodeid
saab endiselt kasutada. See pole toodetest sügav koopia.

## Andmetüübid ja valideerimine

| Andmed | Tüüp | Põhjus |
| --- | --- | --- |
| Nimi | `String` | Tekst; tühi nimi keelatud |
| Hind ja väärtus | `BigDecimal` | Kümnendtäpsusega rahaarvutus, ilma `double` ümardusveata |
| Kogus ja garantii | `int` | Täisarvulised ühikud, negatiivsed väärtused keelatud |
| Säilivuskuupäev | `LocalDate` | Kontrollib päris kuupäeva, kellaaega pole vaja |
| Tooted | `List<Product>` / `ArrayList<Product>` | Dünaamiline eri tooteliikide kogum |

Hind salvestatakse kahe komakohaga; kui teisendamine nõuaks ümardamist, lükatakse
sisend tagasi. Koguse muutmisel kasutatakse ajutiselt `long`-tüüpi tulemust,
et kontrollida ka `int` ülempiiri ületamist. Nullhind ja nullkogus on lubatud.
Sama nimega tooted on keelatud ka erineva suur- ja väiketähtede kasutuse korral.
Vigane sisend annab eestikeelse teate ja viib tagasi peamenüüsse. Poolik lisamine
ei lisa toodet enne kõigi väljade edukat kontrollimist. Rakendus ei teosta
automaatselt aegunud kauba eemaldamist.

## Kuidas lahendust ise muuta

Näiteks muuda `Main`-i konstruktoris Hiire algkogus `3` asemel `5` ning käivita
`./run.sh`. Vali `1`, et näha uut kogust, ja `5`, et kontrollida uut koguväärtust
`128.80 EUR`. Seejärel taasta algkogus `3`; kaasasolevad integratsioonitestid
eeldavad dokumenteeritud algandmeid.

Uue tooteliigi lisamiseks loo `Product`-ist pärinev klass ja kirjuta üle
`getAdditionalInfo()`. Et kasutaja saaks seda menüüst lisada, täienda ka
`Main.addProduct()` meetodit. Lao koguväärtuse arvutust ja üldist kuvamistsüklit
pole selleks vaja muuta.

## Ülesande nõuete vastavus

| Nõue | Teostus või tõend |
| --- | --- |
| Kõigi toodete näitamine | Menüü 1, `getProducts()` ja `printProducts()` |
| Toote lisamine | Menüü 2, kaks tooteliiki, duplikaadikontroll |
| Koguse suurendamine ja vähendamine | Menüü 3, positiivne või negatiivne muutus |
| Nime järgi otsimine | Menüü 4, nimeosa ja tõstutundetu otsing |
| Lao koguväärtus | Menüü 5, hinna ja koguse korrutiste summa |
| Vähemalt kolm toodet | Piim, Leib ja Hiir programmi alguses |
| Seotud klassid ja neli OOP põhimõtet | `Product`, alamklassid ja `Inventory`; selgitused eespool |
| Andmestruktuur | `ArrayList`, lisamine, otsing ja läbimine; põhjendus eespool |
| Vähemalt kolm sisulist commit'i | [Commit'ide loetelu](commitid.md) ja kaasasolev bundle |
| Tööpäevik ja koodi selgitus | Käesolev dokument |
| Töötamise salvestus | [Esitatav salvestus](kaivitustoend.html), lisamine, otsing ja koguväärtus |
