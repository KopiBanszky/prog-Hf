# Programozás alapja 3 - Házi feladat

## Specifikáció - Bánszky Koppány

Alkalmazás - (Leltár adatbázis)
---
Megvalósítandó alkalmazás egy egyszerű, grafikus leltár adatbázis. Hasonlítható akár a Google Drive webes alkalmazáshoz is. Ennél jóval egyszerűbb és feladatorientáltabb az elkészítendő felület.

Megvalósítandó funkciók, képességek
---
1. Fiók rendszer
    - Új felhasználó regisztrálása
    - Meglévő felhasználóval bejelentkezés
    - Fiók törlése
    - Hozzáférési jogosultságok
2. Tárolórendszer
    - Fa struktúra
        - A struktúrában a tárgyakok értelmezendőek levelekként
        - A mappák/kategóriák pedig a csomópontokként
    - Mappákhoz/tárgyakhoz hozzáférések
        - Megtekintő
        - Szerkesztő
        - Adminisztrátor
        - Plusz opció: Első két lehetőség bárki számára elérhető
    - Mappák/Kategóriák az alábbiakat foglalják magukban
        - Név
        - Egyedi azonosító, ami a helyére is utal. (Pl: A az szülő mappáinak nevének első 3 karaktere egymás után majd az adott mappa nevének első 3 karaktere.)
        - Jogosultságok
    - A tárgyak az alábbiakat foglalják magukban
        - Név
        - Egyedi azonosító, ami helyére is utal. (Pl: A az szülő mappáinak nevének első 3 karaktere egymás után és egy megkülönböztető szám a tárgyhoz.)
        - Jogosultságok
        - Leírás (Opcionális)
        - Kép(ek) (Opcionális)
        - Hosszú szöveg (Opcionális)
        - Kommentek

A jogosultságokat adott mappákra, tárgyakra lehessen adni. Ha egy mappa tartalmaz ilyeneket, akkor az abban újonnan létrehozott tárgyak vagy mappák is tartalmazzák ezeket alapértelmezettként.
A jogosultságok az alábbiakat foglalják magukba:

1. Nincs jogosultság hozzáadva
    - Nem tekintheti meg a felhasználó az adott mappát/tárgyat.
    - Ne is jelenjen meg az ilyen.
2. Megtekintő
    - A mappák/tárgyak megjelennek, azokat meg is tudja nyitni.
    - A tárgyakra tud megjegyzést, javaslatot írni.
3. Szerkesztő
    - Mindent amit az előző
    - A mappákat/tárgyakat tudja szerkeszteni.
    - Tud újakat létrehozni
    - Tud törölni
    - Tud megjegyzéséket pipálni
4. Adminisztrátor
    - Mindent amit az előző
    - Módosíthatja a jogosultságokat

Mappák létrehozásakor kötelezően meg kell adni a nevet, illetve van lehetőség a jogosultságok módosítására.

Tárgyak létrehozásakor kötelezően meg kell adni a nevet, az egyedi azonosítót a rendszer generálja automatikusan. Minden más opcionálisan adható.

A már létrehozott tárgyakhoz lehessen megjegyzéseket fűzni. Ezeket listázza is a tárgy megjelenítésénél. A szerkesztő ezeket tudja pipálni, ekkor `megoldottnak` legyen jelölve és rejtse el a felület egy `Megoldottak` szekció alá.

Mappák törlése alapértelmezetten rekurzív legyen, tehát ha vannak leszármazottjai, azok is törlődjenek.

Megfelelő jogosultsággal lehessen az oprendszer fájlrendszeréből hozzáadni struktúrát ehhez a rendszerhez. Ez a következőképp működjön:

Ugyanazzal a fa struktúrával illessze be a környezetbe a mappákat. A neveik legyenek a már megadott neveik. A jogosultságokat a korábban definiált módon állítsa be. A fájlokból csak a .txt kiterjesztésűeket, illetve a képeket vegye figyelembe. A nevük szinén legyen a már megadott. Amennyiben van azonos nevű .txt és kép kiterjesztésű fájl, az ebben a környezetben egy tárgy alá tartozzon. A nevük legyen a közös nevük. A .txt kiterjesztés tartalma a `hosszú szöveg` mezőbe tartozzon, míg a kép a `kép(ek)` alá.

Az adatbázisban lehessen keresni. A keresés mindig az adott részfában található adatokból szűrjön. A keresés terjedjen ki az adatok nevére, azonosítójára, leírására.

---

Grafikus felület
---

### Bejelentkezés/regisztrálás

A felület közepénl egyen 2 szöveg bemenet a megfelelő placeholderekkel (`Név`, `Jelszó`)  
Egy kattintható szöveg (`Regisztrálás`)  
És egy gomb (`Bejelentkezés`)  

Ha a `Regisztrálás` szövegre kattint a felhasználó, jelenjen meg még egy szöveg bemenet `Jelszó mégegyszer` segédszöveggel.  
A kattintható szöveg változzon `Mégse`-re  
A gombe pedig `Regisztrálok`-ra

A `Mégse` szövegre kattintáskor állítsa vissza alapértelmezettre a felületet.

*`vendeg` - `vendeg` bejelentkezési adatok legyenek alapértelmezetten beállítva mint teszt fiók*

### Alap felület, mappák megjelenítése

A felület felső részén, a headerben legyen a keresésre egy szöveg bemenet. Ennek segítő szövege: `Mappa neve - keresés`. Erre fókuszálva, majd entert ütve induljon el a keresés.  
A header jobb oldalán a következő gombok legyenek, megfelelő ikonokkal: Szerkesztés, törlés, mappa létrehozása, tárgy létrehozása.  

Alul, a footerben, jobb oldalt egy gomb, a kijelentkezés legyen.
Bal oldalt pedig egy gomb ami rendezi az adott megjelnített adatokat név szerint, nővevő vagy csökkenő sorban.  
Mellette az aktuálisan megnyitott mappa elérési útja legyen. Az útban a szülőmappák nevei szerepeljenek. Ha egyre rákattint a felhasználó, nyissa meg azt.

A fő felületen először a mappák névvel, alatta kicsvel az azonosítójuk, majd a tárgyak névvel, alatta a leírásuk eleje.
Ez a felállás, hogy először a mappák, majd a tárgyak vannak megjelenítva mindig maradjon meg. A rendezés ezen belül legyen.

Belekattintva egy mappába annak a tartalma legyen a fő felületen megjelenítve.

Egyszer kattintva egy tárgyra a tárgy adatai jobb oldalt egy sávban jelenjenek meg.  
Kétszer kattintva pedig a tárgy adatai legyenek a fő felületen megjelenítve.

### Tárgy felülete

A tárgy mindkét felülete az alábbi módon legyen felépítve.
A bal felső sarokban az egyedi azonosítója jelenjen meg.
Jobb felül 2 gomb, ikonnal: Szerkesztés, törlés.
Egymás alatt a következők jelenjenek meg:

1. Név
1. Leírás
1. Legutóbbi módosítás
1. Képek
1. Hosszú szöveg
1. Megjegyzés hozzáadása (Szöveg bemenet)
1. Megjegyzések
1. Megoldott megjegyzések

### Szerkesztés

Mind a tárgynál mind a mappánál nyisson meg egy új felületet a szerkesztésre.
A mappánál lehessen szerkeszteni a nevet, illetve a jogosultságokat.
A tárgynál pedig az előbbieken túl, a leírást, a hosszú szöveget, valamint lehessen képet hozzáadni, eltávolítani.

A felület alján legyen két gomb, `Mégse` és `Mentés`.

A törlés gomb mindig az aktuálisan megnyitott adatot törölje.

---
***Bánszky Koppány***  
***KK4UWP***
