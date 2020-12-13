package de.hhu.cs.dbs.propra.presentation.rest;

import de.hhu.cs.dbs.propra.application.services.AdminService;
import de.hhu.cs.dbs.propra.application.services.UserService;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class AdminController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;


    @Path("/fahrschulen")
    @POST
    @RolesAllowed({"ADMIN"})
    public Response addFahrschule(@FormDataParam("email") String email,
                                  @FormDataParam("website") String website,
                                  @FormDataParam("bezeichnung") String bezeichnung,
                                  @FormDataParam("adresseid") String adresseid) throws SQLException {

        AdminService adminService = new AdminService(dataSource);

        return adminService.createFahrschule(email, website, bezeichnung, adresseid, securityContext.getUserPrincipal().getName());
    }

    @Path("/fahrzeug")
    @POST
    @RolesAllowed({"ADMIN"})
    public Response addFahrzeug(@FormDataParam("fahrschuleid") String fahrschuleid,
                                  @FormDataParam("fahrzeugklasse") String fahrzeugklasse,
                                  @FormDataParam("kennzeichen") String kennzeichen,
                                  @FormDataParam("hudatum") String hudatum,
                                  @FormDataParam("erstzulassung") String erst) throws SQLException {

        AdminService adminService = new AdminService(dataSource);

        return adminService.createFahrzeug(fahrschuleid, fahrzeugklasse, kennzeichen, hudatum, erst, securityContext.getUserPrincipal().getName());
    }

    @Path("/theorieuebungen")
    @POST
    @RolesAllowed({"ADMIN"})
    public Response addUebung(@FormDataParam("fahrschuleid") String fahrschuleid,
                                @FormDataParam("themabezeichnung") String themabezeichnung,
                                @FormDataParam("dauer") String dauer,
                                @FormDataParam("verpflichtend") String verpflichtend) throws SQLException {

        AdminService adminService = new AdminService(dataSource);

        return adminService.createUebung(fahrschuleid, themabezeichnung, dauer, verpflichtend, securityContext.getUserPrincipal().getName());
    }

    @Path("/pruefungen")
    @POST
    @RolesAllowed({"ADMIN"})
    public Response addPruefung(@FormDataParam("fahrschuelerid") String fahrschuelerid,
                              @FormDataParam("gebuehr") String gebuehr,
                              @FormDataParam("typ") String typ,
                              @FormDataParam("ergebnis") String ergebnis) throws SQLException {

        AdminService adminService = new AdminService(dataSource);

        return adminService.createPruefung(fahrschuelerid, gebuehr, typ, ergebnis, securityContext.getUserPrincipal().getName());
    }

    @Path("/pruefungen")
    @GET
    @RolesAllowed({"ADMIN"})
    public Response getPruefung(@QueryParam("fahrschuelerid") Integer fahrschuelerid,
                                @QueryParam("gebuehr") Double gebuehr,
                                @QueryParam("typ") Boolean typ,
                                @QueryParam("ergebnis") Boolean ergebnis) throws SQLException {

        AdminService adminService = new AdminService(dataSource);

        return adminService.getPruefung(fahrschuelerid, gebuehr, typ, ergebnis);
    }
}
