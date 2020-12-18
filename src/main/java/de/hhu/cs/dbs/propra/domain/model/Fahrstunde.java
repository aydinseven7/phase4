package de.hhu.cs.dbs.propra.domain.model;


import lombok.Data;

@Data
public class Fahrstunde {
    private int fahrschuleid;
    private int fahrschuelerid;
    private double preis;
    private double dauer;
    private String typ;
}
