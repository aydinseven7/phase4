PRAGMA auto_vacuum = 1;
PRAGMA encoding = "UTF-8";
PRAGMA foreign_keys = 1;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;

DROP TABLE IF EXISTS "Adresse";
DROP TABLE IF EXISTS "Nutzer";
DROP TABLE IF EXISTS "Öffnungszeiten";
DROP TABLE IF EXISTS "Fahrzeugklasse";
DROP TABLE IF EXISTS "Schüler";
DROP TABLE IF EXISTS "Admin";
DROP TABLE IF EXISTS "Fahrschule";
DROP TABLE IF EXISTS "Prüfung";
DROP TABLE IF EXISTS "Führerschein";
DROP TABLE IF EXISTS "Fahrlehrer";
DROP TABLE IF EXISTS "theoretische_Übung";
DROP TABLE IF EXISTS "Fahrstunde";
DROP TABLE IF EXISTS "Fahrzeug";
DROP TABLE IF EXISTS "Fahrschule_hat_Öffnungszeiten";
DROP TABLE IF EXISTS "Führerschein_Erlaubt_Fahrzeugklasse";
DROP TABLE IF EXISTS "Fahrschule_besitzt_Adresse";
DROP TABLE IF EXISTS "Schüler_Belegt_Theoretische_Übung";
DROP TABLE IF EXISTS "Schüler_Belegt_Prüfung";


CREATE TABLE IF NOT EXISTS "Adresse" (
	"id"	INTEGER UNIQUE,
	"Stadt"	varchar(30) NOT NULL CHECK(trim("Stadt") NOT LIKE '' AND "Stadt" NOT GLOB '*[-~]*' AND "Stadt" NOT GLOB '*[0-9]*'),
	"PLZ"	INTEGER NOT NULL CHECK("PLZ" BETWEEN 10000 AND 99999),
	"Strasse"	varchar(30) NOT NULL CHECK(trim("Strasse") NOT LIKE '' AND "Strasse" NOT GLOB '*[-~]*' AND "Strasse" NOT GLOB '*[0-9]*'),
	"Hausnummer"	varchar(10) NOT NULL CHECK(trim("Hausnummer") NOT LIKE '' AND "Hausnummer" NOT GLOB '*[-~]*'),
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE IF NOT EXISTS "Nutzer" (
	"Email"	varchar(30) NOT NULL CHECK(ltrim(lower("Email"), 'qwertzuiopüasdfghjklöäyxcvbnm1234567890') LIKE '@%.%' AND ltrim(lower("Email"), '@qwertzuiopüasdfghjklöäyxcvbnm1234567890') LIKE '.%' AND rtrim(lower("Email"), 'qwertzuiopüasdfghjklöäyxcvbnm') LIKE '%@%.' AND "Email" NOT LIKE '@%.%' AND "Email" NOT LIKE '%@.%' AND "Email" NOT LIKE '%@%.') COLLATE NOCASE UNIQUE,
	"Passwort"	varchar(50) NOT NULL CHECK(length("Passwort") >= 5 AND "Passwort" NOT GLOB '*[0-9][0-9]*' AND "Passwort" GLOB '*[A-Z]*' AND "Passwort" GLOB '*[0-9]*[0-9]*'),
	"Vorname"	varchar(30) NOT NULL CHECK(trim("Vorname") NOT LIKE '' AND "Vorname" NOT GLOB '*[-~]*'),
	"Nachname"	varchar(30) NOT NULL CHECK(trim("Nachname") NOT LIKE '' AND "Nachname" NOT GLOB '*[-~]*'),
	PRIMARY KEY("Email")
);

CREATE TABLE IF NOT EXISTS "Oeffnungszeiten" (
	"id"	INTEGER UNIQUE,
	"Tag"	INTEGER NOT NULL CHECK("Tag" BETWEEN 0 AND 6) UNIQUE,
	"Anfang"	varchar(8) NOT NULL CHECK(trim("Anfang") NOT LIKE '' AND "Anfang" IS time("Anfang")),
	"Ende"	varchar(8) NOT NULL CHECK("Ende" IS time("Ende")),
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE IF NOT EXISTS "Fahrzeugklasse" (
	"Bezeichnung"	varchar(30) NOT NULL CHECK(trim("Bezeichnung") NOT LIKE '' AND "Bezeichnung" NOT GLOB '*[-~]*' AND "Bezeichnung" NOT GLOB '*[0-9]*') UNIQUE,
	PRIMARY KEY("Bezeichnung")
);

CREATE TABLE IF NOT EXISTS "Pruefung" (
	"id"	INTEGER UNIQUE,
	"Typ"	INTEGER NOT NULL CHECK("Typ" =0 OR "Typ" = 1),
	"Teilnahmegebuehr"	Float NOT NULL CHECK("Teilnahmegebuehr" > 0 AND "Teilnahmegebuehr" = round("Teilnahmegebuehr", 2)),
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE IF NOT EXISTS "Admin" (
	"Email"	varchar(30) NOT NULL UNIQUE,
	"Telefonnummer"	varchar(20) NOT NULL CHECK("Telefonnummer" LIKE '0%' AND substr("Telefonnummer", 1) GLOB '*[0-9]*') UNIQUE,
	PRIMARY KEY("Email"),
	FOREIGN KEY("Email") REFERENCES "Nutzer"("Email") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "Fahrschule" (
	"Email"	varchar(30) NOT NULL CHECK(ltrim(lower("Email"), 'qwertzuiopüasdfghjklöäyxcvbnm1234567890') LIKE '@%.%' AND ltrim(lower("Email"), '@qwertzuiopüasdfghjklöäyxcvbnm1234567890') LIKE '.%' AND rtrim(lower("Email"), 'qwertzuiopüasdfghjklöäyxcvbnm') LIKE '%@%.' AND "Email" NOT LIKE '@%.%' AND "Email" NOT LIKE '%@.%' AND "Email" NOT LIKE '%@%.') COLLATE NOCASE UNIQUE,
	"Website"	varchar(30) NOT NULL CHECK(trim("Website") NOT LIKE '' AND "Website" NOT GLOB '*[-~]' AND "Website" LIKE 'https://%') COLLATE NOCASE,
	"Bezeichnung"	varchar(30) NOT NULL CHECK(trim("Bezeichnung") NOT LIKE '' AND "Bezeichnung" NOT GLOB '*[-~]*' AND "Bezeichnung" NOT GLOB '*[0-9]*'),
	"Admin"	varchar(30) NOT NULL,
	PRIMARY KEY("Email"),
	FOREIGN KEY("Admin") REFERENCES "Admin"("Email") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "Schueler" (
	"Email"	varchar(30) NOT NULL UNIQUE,
	"Geschlecht"	varchar(1) CHECK("Geschlecht" LIKE "m" OR "Geschlecht" LIKE "w" OR "Geschlecht" LIKE "d"),
	"Adresse"	INTEGER NOT NULL,
	PRIMARY KEY("Email"),
	FOREIGN KEY("Email") REFERENCES "Nutzer"("Email") ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY("Adresse") REFERENCES "Adresse"("id") ON DELETE cascade ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "Fuehrerschein" (
	"Schueler"	varchar(30) NOT NULL,
	"Austellungsdatum"	varchar(20) NOT NULL CHECK(trim("Austellungsdatum") NOT LIKE '' AND "Austellungsdatum" IS date("Austellungsdatum")),
	"Gueltigkeitsdatum"	varchar(20) NOT NULL CHECK(trim("Gueltigkeitsdatum") NOT LIKE '' AND "Gueltigkeitsdatum" IS date("Gueltigkeitsdatum")),
	"id"	INTEGER UNIQUE,
	PRIMARY KEY("id"),
	FOREIGN KEY("Schueler") REFERENCES "Schueler"("Email") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "Fahrlehrer" (
	"Email"	varchar(30) NOT NULL UNIQUE,
	"Fahrlehrerlizenz"	varchar(20) NOT NULL CHECK(trim("Fahrlehrerlizenz") NOT LIKE '' AND "Fahrlehrerlizenz" IS date("Fahrlehrerlizenz")),
	PRIMARY KEY("Email"),
	FOREIGN KEY("Email") REFERENCES "Nutzer"("Email")
);

CREATE TABLE IF NOT EXISTS "theoretische_Uebung" (
	"id"	INTEGER UNIQUE,
	"Pflicht"	INTEGER NOT NULL CHECK("Pflicht" = 1 OR "Pflicht" = 0),
	"Dauer"	INTEGER NOT NULL CHECK("Dauer" > 0),
	"Thema"	varchar(30) NOT NULL CHECK(trim("Thema") NOT LIKE '' AND "Thema" GLOB '*[a-zA-Z]*'),
	"Fahrschule"	varchar(30) NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("Fahrschule") REFERENCES "Fahrschule"("Email") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "Fahrstunde" (
	"Schueler"	INTEGER NOT NULL,
	"Fahrlehrer"	INTEGER NOT NULL,
	"Fahrschule"	INTEGER NOT NULL,
	"Dauer"	INTEGER NOT NULL CHECK("Dauer" > 0 AND "Dauer" % 45 = 0),
	"Typ"	INTEGER NOT NULL CHECK(trim("Typ") NOT LIKE '' AND "Typ" GLOB '*[^-~]*' AND "Typ" NOT GLOB '*[0-9]*'),
	"Preis"	Float NOT NULL CHECK("Preis" > 0 AND "Preis" = round("Preis", 2)),
	"id"	INTEGER UNIQUE,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("Fahrschule") REFERENCES "Fahrschule"("Email") ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY("Fahrlehrer") REFERENCES "Fahrlehrer"("Email") ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY("Schueler") REFERENCES "Schueler"("Email") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "Fahrzeug" (
	"Fahrzeugklasse"	varchar(30) NOT NULL,
	"Fahrschule"	varchar(30) NOT NULL,
	"Kennzeichen"	varchar(12) NOT NULL CHECK(trim("Kennzeichen") NOT LIKE '') UNIQUE,
	"HU-Eintrag"	varchar(30) NOT NULL CHECK(trim("HU-Eintrag") NOT LIKE '' AND "HU-Eintrag" IS date("HU-Eintrag")),
	"Erstzulassung"	varchar(30) NOT NULL CHECK(trim("Erstzulassung") NOT LIKE '' AND "Erstzulassung" IS date("Erstzulassung")),
	PRIMARY KEY("Kennzeichen"),
	FOREIGN KEY("Fahrschule") REFERENCES "Fahrschule"("Email") ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY("Fahrzeugklasse") REFERENCES "Fahrzeugklasse"("Bezeichnung") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "Fuehrerschein_Erlaubt_Fahrzeugklasse" (
	"Fuehrerschein"	INTEGER NOT NULL,
	"Fahrzeugklasse"	varchar(30) NOT NULL,
	"id"	INTEGER UNIQUE,
	FOREIGN KEY("Fuehrerschein") REFERENCES "Fuehrerschein"("id") ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY("Fahrzeugklasse") REFERENCES "Fahrzeugklasse"("Bezeichnung") ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE IF NOT EXISTS "Fahrschule_besitzt_Adresse" (
	"Fahrschule"	varchar(30) NOT NULL,
	"Adresse"	INTEGER NOT NULL,
	"id"	INTEGER UNIQUE,
	FOREIGN KEY("Adresse") REFERENCES "Adresse"("id") ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY("Fahrschule") REFERENCES "Fahrschule"("Email") ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE IF NOT EXISTS "Fahrschule_hat_Oeffnungszeiten" (
	"Fahrschule"	varchar(30) NOT NULL,
	"Oeffnungszeiten"	INTEGER NOT NULL,
	"id"	INTEGER UNIQUE,
	FOREIGN KEY("Oeffnungszeiten") REFERENCES "Oeffnungszeiten"("id"),
	FOREIGN KEY("Fahrschule") REFERENCES "Fahrschule"("Email") ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE IF NOT EXISTS "Schueler_Belegt_Pruefung" (
	"Schueler"	INTEGER NOT NULL,
	"Pruefung"	INTEGER NOT NULL,
	"Erfolgreich"	INTEGER NOT NULL CHECK("Erfolgreich" = 1 OR "Erfolgreich" = 0),
	"id"	INTEGER UNIQUE,
	FOREIGN KEY("Schueler") REFERENCES "Schueler"("Email") ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY("Pruefung") REFERENCES "Pruefung"("id") ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE IF NOT EXISTS "Schueler_Belegt_Theoretische_Uebung" (
	"Schueler"	varchar(30) NOT NULL,
	"theoretische_Uebung"	INTEGER NOT NULL,
	"id"	INTEGER UNIQUE,
	FOREIGN KEY("theoretische_Uebung") REFERENCES "theoretische_Uebung"("id") ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY("Schueler") REFERENCES "Schueler"("Email") ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY("id" AUTOINCREMENT)
);


DROP TRIGGER IF EXISTS "checkFahrstunden";
CREATE TRIGGER checkFahrstunden BEFORE INSERT ON Schueler_Belegt_Pruefung
BEGIN
SELECT RAISE(ABORT, "Der Schüler hat noch nicht genug Fahrstunden absolviert!")
WHERE EXISTS (SELECT Typ FROM Pruefung
	WHERE Typ = 1 AND (SELECT sum(Fahrstunde.Dauer)
        FROM Fahrstunde, Schueler_Belegt_Pruefung
        WHERE Fahrstunde.Schueler = Schueler_Belegt_Pruefung.Schueler
        	AND Pruefung.id=Schueler_Belegt_Pruefung.Pruefung)<180);
END;
/*Trigger checkTheorie funktioniert nicht, er löst immer aus 
Theorie-Typ=0
Praxis-Typ=1
Pflicht=TRUE(0) OR FALSE(1)*/

DROP TRIGGER IF EXISTS "checkTheorie";
CREATE TRIGGER checkTheorie
BEFORE INSERT ON Schueler_Belegt_Pruefung FOR EACH ROW
BEGIN
SELECT RAISE(ABORT,"Der Schüler hat noch nicht genug Pflicht-Übungen besucht!") 
	WHERE EXISTS (SELECT Typ
		FROM Pruefung
		WHERE Typ = 0 AND ((SELECT count(DISTINCT theoretische_Uebung.Thema)
			FROM Schueler_Belegt_Pruefung, Schueler_Belegt_Theoretische_Uebung, theoretische_Uebung
			WHERE Schueler_Belegt_Theoretische_Uebung.Schueler = Schueler_Belegt_Pruefung.Schueler
				AND Pruefung.id = Schueler_Belegt_Pruefung.Pruefung
				AND theoretische_Uebung.id = Schueler_Belegt_Theoretische_Uebung.theoretische_Uebung
				AND theoretische_Uebung.Pflicht = 0)<3));
END;

DROP TRIGGER IF EXISTS "openingTimes";
CREATE TRIGGER openingTimes
BEFORE INSERT ON Oeffnungszeiten FOR EACH ROW
BEGIN
SELECT RAISE(ABORT, 'Die Öffnungszeit liegt nach der Schließung!') WHERE EXISTS (SELECT id
	FROM Oeffnungszeiten
	WHERE ((time(Anfang) - time(Ende))>0));
END;