package de.hhu.cs.dbs.propra.domain.model;

import lombok.Data;

@Data
public class Fahrschule {

    private Integer fahrschuleid;

    private Integer adresseid;

    private String email;

    private String website;

    private String bezeichnung;

}