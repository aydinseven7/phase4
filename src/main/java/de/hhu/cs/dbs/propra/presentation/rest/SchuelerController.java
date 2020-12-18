package de.hhu.cs.dbs.propra.presentation.rest;

import de.hhu.cs.dbs.propra.application.services.AdminService;
import de.hhu.cs.dbs.propra.application.services.LehrerService;
import de.hhu.cs.dbs.propra.application.services.SchuelerService;
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
public class SchuelerController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("/theorieuebungen/{theorieuebungid}/fahrschueler")
    @POST
    @RolesAllowed({"SCHUELER"})
    public Response addFahrschuelerToUebung(@PathParam("theorieuebungid") String theorieuebungid) throws SQLException {

        SchuelerService schuelerService = new SchuelerService(dataSource);

        return schuelerService.addFahrschuelerToUebung(theorieuebungid, securityContext.getUserPrincipal().getName());
    }
}
