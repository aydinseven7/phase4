package de.hhu.cs.dbs.propra.domain.model;


import lombok.Data;

@Data
public class Adresse {
    private int adresseid;
    private String plz;
    private String strasse;
    private String stadt;
    private String hausnummer;
}
