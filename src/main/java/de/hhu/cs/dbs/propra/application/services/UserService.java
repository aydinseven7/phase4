package de.hhu.cs.dbs.propra.application.services;


import de.hhu.cs.dbs.propra.domain.model.Fahrlehrer;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;


public class UserService {
    private DataSource dataSource;

    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Response createFahrschueler(String email, String password, String vorname, String nachname, String geschlecht, String addressId) throws SQLException {

        // User speichern
        try {
            addUser(email, password, vorname, nachname);

            if(checkAdressExists(addressId)){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Connection connection;
            PreparedStatement preparedStatement;

            //Fahrschueler speichern

            String sql2 = "INSERT INTO Schueler (Email, Geschlecht, Adresse) VALUES (?, ?, ?)";

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, geschlecht);
            preparedStatement.setString(3, addressId);

            preparedStatement.executeUpdate();

            Long id = preparedStatement.getGeneratedKeys().getLong(1);

            preparedStatement.closeOnCompletion();

            return Response.status(Response.Status.CREATED).header("Location",
                    "fahrschueler/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response createAdmin(String email, String password, String vorname, String nachname, String telefonnummer) throws SQLException {

        // User speichern
        try {
            addUser(email, password, vorname, nachname);

            Connection connection;
            PreparedStatement preparedStatement;

            //Fahrschueler speichern

            String sql2 = "INSERT INTO Admin (Email, Telefonnummer) VALUES (?, ?)";

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, telefonnummer);

            preparedStatement.executeUpdate();

            Long id = preparedStatement.getGeneratedKeys().getLong(1);

            preparedStatement.closeOnCompletion();

            return Response.status(Response.Status.CREATED).header("Location",
                    "admins/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    public Response createFahrlehrer(String email, String password, String vorname, String nachname, String lizenzdatum) throws SQLException {

        // User speichern
        try {
            addUser(email, password, vorname, nachname);

            Connection connection;
            PreparedStatement preparedStatement;

            //Fahrschueler speichern

            String sql2 = "INSERT INTO Fahrlehrer (Email, Fahrlehrerlizenz) VALUES (?, ?)";

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, lizenzdatum);

            preparedStatement.executeUpdate();

            Long id = preparedStatement.getGeneratedKeys().getLong(1);

            preparedStatement.closeOnCompletion();

            return Response.status(Response.Status.CREATED).header("Location",
                    "fahrlehrer/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private void addUser(String email, String password, String vorname, String nachname) throws SQLException {
        String sql = "INSERT INTO Nutzer (Email, Passwort, Vorname, Nachname) VALUES (?, ?, ? , ?)";


        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, email);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, vorname);
        preparedStatement.setString(4, nachname);

        preparedStatement.executeUpdate();
        preparedStatement.closeOnCompletion();
    }

    private boolean checkAdressExists(String adressId) throws SQLException {
        String sql = "SELECT ID FROM Adresse WHERE ID = ?";

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, adressId);
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        String[] strings = null;
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount();i++) {
            String name = metaData.getColumnName(i);
            list.add(name);
        }
        strings = list.toArray(String[]::new);
        List<Map<String, Object>> entities = resultSetToList(strings, resultSet);

        preparedStatement.closeOnCompletion();

        return entities.isEmpty();
    }

    private List<Map<String, Object>> resultSetToList(String[] tablenames, ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Object> entity;
        getEntities(tablenames, resultSet, entities);
        resultSet.close();
        return entities;
    }

    private void getEntities(String[] tablenames, ResultSet resultSet,
                             List<Map<String, Object>> entities) throws SQLException {
        Map<String, Object> entity;
        while (resultSet.next()) {
            entity = new HashMap<>();
            for(int i = 1; i <= tablenames.length; i++){
                entity.put(tablenames[i-1], resultSet.getObject(i));
            }
            entities.add(entity);
        }
    }

    public Response getFahrlehrer(String lizenzdatum, String nachname) throws SQLException {

        String sql = "SELECT Fahrlehrer.RowId, Fahrlehrer.Fahrlehrerlizenz, Nutzer.Email, Nutzer.Passwort, Nutzer.Vorname, Nutzer.Nachname FROM Fahrlehrer,Nutzer WHERE Fahrlehrer.Email = Nutzer.Email AND ((strftime('%Y-%m-%d', Fahrlehrer.Fahrlehrerlizenz) - strftime('%Y-%m-%d', ?)) >= 0 OR ? IS NULL) AND (lower(Nutzer.Nachname) = lower(?) OR ? IS NULL)";

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, lizenzdatum);
        preparedStatement.setString(2, lizenzdatum);
        preparedStatement.setString(3, nachname);
        preparedStatement.setString(4, nachname);
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        
        String[] strings = null;
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount();i++) {
            String name = metaData.getColumnName(i);
            list.add(name);
        }
        
        strings = list.toArray(String[]::new);
        List<Map<String, Object>> entities = resultSetToList(strings, resultSet);

        List<Fahrlehrer> fahrlehrer = new ArrayList<>();

        entities.forEach(e -> {
            Fahrlehrer tempFahrlehrer = new Fahrlehrer();

            tempFahrlehrer.setId(Integer.valueOf(e.get("rowid").toString()));
            tempFahrlehrer.setEmail(e.get("Email").toString());
            tempFahrlehrer.setLizenzdatum(e.get("Fahrlehrerlizenz").toString());
            tempFahrlehrer.setPasswort(e.get("Passwort").toString());
            tempFahrlehrer.setVorname(e.get("Vorname").toString());
            tempFahrlehrer.setNachname(e.get("Nachname").toString());

            fahrlehrer.add(tempFahrlehrer);
        });
        preparedStatement.closeOnCompletion();

        return Response.status(Response.Status.OK).entity(fahrlehrer).build();
    }
}
