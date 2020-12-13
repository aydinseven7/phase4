package de.hhu.cs.dbs.propra.domain.model;

import lombok.Data;

@Data
public class Fahrschule {

    private int fahrschulId;

    private int adressId;

    private String email;

    private String website;

    private String bezeichnung;

}