INSERT INTO "Nutzer" ("Email", "Passwort", "Vorname", "Nachname")
VALUES ('test@gmail.com', 'E1E1EEEEE', 'TestVorname', 'TestNachname'),
       ('ist@test.de', 'E1E1EEEEE', 'Thomas', 'Tester'),
       ('hhu@test.de', 'E1E1EEEEE', 'Heinrich', 'Heine'),
       ("drei@hhu.de", "E1E1EEEEE", "drei", "Drei"),
('vier@hhu.de', 'E1E1EEEEE', 'vier', 'Vier'),
('vier1@hhu.de', 'E1E1EEEEE', 'vier', 'Vier'),
('vier2@hhu.de', 'E1E1EEEEE', 'vier', 'Vier'),
('fuenf@hhu.de', 'E1E1EEEEE', 'fünf', 'Fünf'),
('sechs@hhu.de', 'E1E1EEEEE', 'sechs', 'Sechs');

INSERT INTO "Adresse" ("id", "Stadt", "PLZ", "Strasse", "Hausnummer")
VALUES (1, 'Düsseldorf', 40647, 'TestStrasse', '12'),
       (2, 'Köln', 30647, 'StrasseKoeln', '12'),
       (3, 'Frankfurt', 20647, 'StrasseFrankfurt', '12');

INSERT INTO "Öffnungszeiten"("id","Tag", "Anfang", "Ende")
VALUES (1, 0, '08:00:00', '20:00:00'),
       (2, 1, '10:00:00', '08:00:00');

INSERT INTO "Fahrzeugklasse"("Bezeichnung")
VALUES('AM'),
        ('B');

INSERT INTO "Schueler"("Email","Geschlecht","Adresse")
VALUES('test@gmail.com','m',1),
('vier2@hhu.de','m',1),
('vier1@hhu.de','m',1),
("ist@test.de",'w',2);

INSERT INTO "Admin"("Email", "Telefonnummer")
VALUES("drei@hhu.de", '01111111111'),
("vier@hhu.de",'02222222222');

INSERT INTO "Fahrschule"("Email", "Website", "Bezeichnung", "Admin")
VALUES('fahr1@hhu.de', 'https://eins', 'Erste', "drei@hhu.de"),
('fahr2@hhu.de', 'https://zwei', 'Zweite',"vier@hhu.de");

INSERT INTO "Prüfung"("id", "Typ", "Teilnahmegebühr")
VALUES(0,0,50.36566),
(1,0,30.55555);

INSERT INTO "Führerschein"("Schüler", "Austellungsdatum", "Gültigkeitsdatum", "id")
VALUES('test@gmail.com', date('now'), '2021-10-07', 0),
("ist@test.de", date('now'), '2021-10-22', 1);

INSERT INTO "Fahrlehrer"("Email", "Fahrlehrerlizenz")
VALUES('hhu@test.de', date('now')),
('vier@hhu.de', date('now')),
('sechs@hhu.de', date('now')),
('fuenf@hhu.de', date('now'));

INSERT INTO "theoretische_Übung"("id", "Pflicht", "Dauer", "Thema", "Fahrschule")
VALUES(0, 0, 80, 'Blick', 'fahr1@hhu.de'),
(1, 0, 80, 'a', 'fahr1@hhu.de'),
(2, 0, 80, 'b', 'fahr1@hhu.de'),
(3, 0, 80, 'c', 'fahr1@hhu.de'),
(4, 1, 60, 'Vorfahrt','fahr2@hhu.de');

INSERT INTO "Fahrstunde"("Schüler", "Fahrlehrer", "Fahrschule", "Dauer", "Typ", "Preis", "id")
VALUES('test@gmail.com','hhu@test.de','fahr1@hhu.de', 180, 'Autobahn', 120, 0),
("ist@test.de", 'fuenf@hhu.de', 'fahr2@hhu.de', 180,'Autobahn', 120, 1),
("vier1@hhu.de", 'sechs@hhu.de', 'fahr2@hhu.de', 180,'Autobahn', 120, 2),
("vier2@hhu.de", 'sechs@hhu.de', 'fahr2@hhu.de', 180,'Autobahn', 120, 3),
("vier2@hhu.de", 'fuenf@hhu.de', 'fahr2@hhu.de', 180,'Autobahn', 120, 4);

INSERT INTO "Fahrzeug"("Fahrzeugklasse", "Fahrschule", "Kennzeichen", "HU-Eintrag", "Erstzulassung")
VALUES('AM', 'fahr1@hhu.de', 'ME1', '2021-10-07','2013-10-07'),
('B', 'fahr2@hhu.de', 'ME2', '2021-10-22','2013-10-22');

INSERT INTO "Fahrschule_hat_Öffnungszeiten"("Fahrschule", "Öffnungszeiten", "id")
VALUES('fahr1@hhu.de', 1, 0),
('fahr2@hhu.de', 2, 1);

INSERT INTO "Führerschein_Erlaubt_Fahrzeugklasse"("Führerschein", "Fahrzeugklasse", "id")
VALUES(0, 'AM',0),
(1,'B',1);

INSERT INTO "Fahrschule_besitzt_Adresse"("Fahrschule", "Adresse", "id")
VALUES('fahr1@hhu.de', 1, 0),
('fahr2@hhu.de', 2, 1);

INSERT INTO "Schüler_Belegt_Theoretische_Übung"("Schüler", "theoretische_Übung", "id")
VALUES('test@gmail.com', 0, 0),
('test@gmail.com', 1, 5),
('test@gmail.com', 2, 6),
("ist@test.de", 0, 1),
("ist@test.de", 1, 2),
("ist@test.de", 2, 3),
("ist@test.de", 3, 4);

INSERT INTO "Schüler_Belegt_Prüfung"("Schüler", "Prüfung", "Erfolgreich", "id")
VALUES('test@gmail.com', 1, 0, 0),
("ist@test.de", 0, 1, 1);