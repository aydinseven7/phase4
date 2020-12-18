package de.hhu.cs.dbs.propra.presentation.rest;

import de.hhu.cs.dbs.propra.application.services.AdminService;
import de.hhu.cs.dbs.propra.application.services.LehrerService;
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
public class LehrerController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;


    @Path("/fahrstunden")
    @POST
    @RolesAllowed({"FAHRLEHRER"})
    public Response addFahrstunde(@FormDataParam("typ") String typ,
                                  @FormDataParam("dauer") String dauer,
                                  @FormDataParam("preis") String preis,
                                  @FormDataParam("fahrzeugid") String fahrzeugid,
                                  @FormDataParam("fahrschuelerid") String fahrschuelerid) throws SQLException {

        LehrerService lehrerService = new LehrerService(dataSource);

        return lehrerService.createFahrstunde(typ, dauer, preis, fahrschuelerid, fahrzeugid, securityContext.getUserPrincipal().getName());
    }

    @Path("/fahrstunden")
    @GET
    @RolesAllowed({"FAHRLEHRER"})
    public Response addFahrstunde(@QueryParam("dauer") String dauer) throws SQLException {

        LehrerService lehrerService = new LehrerService(dataSource);

        return lehrerService.getFahrstunde(dauer, securityContext.getUserPrincipal().getName());
    }
}
