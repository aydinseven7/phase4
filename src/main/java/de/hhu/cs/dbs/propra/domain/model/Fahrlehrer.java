package de.hhu.cs.dbs.propra.domain.model;


import lombok.Data;

@Data
public class Fahrlehrer {
    private int id;

    private String email;

    private String lizenzdatum;

    private String nachname;

    private String vorname;

    private String passwort;


}
