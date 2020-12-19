package de.hhu.cs.dbs.propra.presentation.rest;

import de.hhu.cs.dbs.propra.application.services.UserService;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;


    @Path("/fahrschueler")
    @POST
    public Response addFahrschueler(@FormDataParam("email") String email,
                                    @FormDataParam("passwort") String password,
                                    @FormDataParam("vorname") String vorname,
                                    @FormDataParam("nachname") String nachname,
                                    @FormDataParam("geschlecht") String geschlecht,
                                    @FormDataParam("addressId") Integer addressId) throws SQLException {

        UserService userService = new UserService(dataSource);

        return userService.createFahrschueler(email, password, vorname, nachname, geschlecht, addressId);
    }

    @Path("/admins")
    @POST
    public Response addAdmin(@FormDataParam("email") String email,
                                    @FormDataParam("passwort") String password,
                                    @FormDataParam("vorname") String vorname,
                                    @FormDataParam("nachname") String nachname,
                                    @FormDataParam("telefonnummer") String telefonnummer) throws SQLException {

        UserService userService = new UserService(dataSource);

        return userService.createAdmin(email, password, vorname, nachname, telefonnummer);
    }

    @Path("/fahrlehrer")
    @POST
    public Response addFahrlehrer(@FormDataParam("email") String email,
                             @FormDataParam("password") String password,
                             @FormDataParam("vorname") String vorname,
                             @FormDataParam("nachname") String nachname,
                             @FormDataParam("lizenzdatum") String lizenzdatum) throws SQLException {

        UserService userService = new UserService(dataSource);

        return userService.createFahrlehrer(email, password, vorname, nachname, lizenzdatum );
    }
    @Path("/fahrlehrer")
    @GET
    public Response getFahrlehrer(@QueryParam("lizenzdatum") String lizenzdatum,
                                  @QueryParam("nachname") String nachname) throws SQLException {

        UserService userService = new UserService(dataSource);

        return userService.getFahrlehrer(lizenzdatum, nachname);
    }

    @Path("/fahrschulen")
    @GET
    public Response getFahrschule(@QueryParam("bezeichnung") String bezeichnung,
                                  @QueryParam("fahrzeugklasse") String klasse) throws SQLException {

        UserService userService = new UserService(dataSource);

        return userService.getFahrschule(bezeichnung, klasse);
    }

    @Path("/fahrzeuge")
    @GET
    public Response getFahrzeuge(@QueryParam("kennzeichen") String kennzeichen,
                                  @QueryParam("erstzulassungsdatum") String erst) throws SQLException {

        UserService userService = new UserService(dataSource);

        return userService.getFahrzeuge(kennzeichen, erst);
    }

    @Path("/adressen")
    @GET
    public Response getAdressen(@QueryParam("hausnummer") String hausnummer) throws SQLException {

        UserService userService = new UserService(dataSource);

        return userService.getAdressen(hausnummer);
    }

    @Path("/theorieuebungen")
    @GET
    public Response getUebung(@QueryParam("themabezeichnung") String themabezeichnung,
                              @QueryParam("dauer") Integer dauer,
                              @QueryParam("verpflichtend") Boolean verpflichtend,
                              @QueryParam("fahrschuleid") Integer fahrschuleid) throws SQLException {

        UserService userService = new UserService(dataSource);

        return userService.getUebung(themabezeichnung, dauer, verpflichtend, fahrschuleid);
    }

}

