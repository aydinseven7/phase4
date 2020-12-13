package de.hhu.cs.dbs.propra.domain.model;


import lombok.Data;

@Data
public class Fahrzeug {
    private int fahrzeugid;
    private int fahrschuleid;
    private String hudatum;
    private String erstzulassung;
    private String kennzeichen;
}
