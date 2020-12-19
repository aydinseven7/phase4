package de.hhu.cs.dbs.propra.domain.model;


import lombok.Data;

@Data
public class Pruefung {
    private Integer fahrschuelerid;
    private Double gebuehr;
    private Boolean typ;
    private Boolean ergebnis;
}
