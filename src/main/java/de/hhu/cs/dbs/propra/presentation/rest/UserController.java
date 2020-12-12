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
                                    @FormDataParam("password") String password,
                                    @FormDataParam("vorname") String vorname,
                                    @FormDataParam("nachname") String nachname,
                                    @FormDataParam("geschlecht") String geschlecht,
                                    @FormDataParam("addressId") String addressId) throws SQLException {

        UserService userService = new UserService(dataSource);

        return userService.createFahrschueler(email, password, vorname, nachname, geschlecht, addressId);
    }

    @Path("/admins")
    @POST
    public Response addAdmin(@FormDataParam("email") String email,
                                    @FormDataParam("password") String password,
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
    public Response getFahrlehrer(@QueryParam("nachname") String nachname,
                                  @QueryParam("lizenzdatum") String lizenzdatum) throws SQLException {

        UserService userService = new UserService(dataSource);

        return userService.getFahrlehrer(nachname, lizenzdatum);
    }
}

