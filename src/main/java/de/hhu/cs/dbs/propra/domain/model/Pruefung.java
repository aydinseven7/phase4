package de.hhu.cs.dbs.propra.domain.model;


import lombok.Data;

@Data
public class Pruefung {
    private int fahrschuelerid;
    private double gebuehr;
    private boolean typ;
    private boolean ergebnis;
}
