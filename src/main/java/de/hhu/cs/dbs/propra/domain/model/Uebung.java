package de.hhu.cs.dbs.propra.domain.model;


import lombok.Data;

@Data
public class Uebung {
    private Integer theorieuebungid;
    private Integer fahrschuleid;
    private String themabezeichnung;
    private Boolean verpflichtend;
    private Integer dauer;
}
