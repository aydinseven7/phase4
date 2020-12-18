package de.hhu.cs.dbs.propra.application.services;

import de.hhu.cs.dbs.propra.domain.model.*;

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

public class LehrerService{
    private DataSource dataSource;

    public LehrerService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Response createFahrstunde(String typ, String dauer, String preis, String fahrschuelerid, String fahrzeugid, String fahrlehrer) throws SQLException{
        try{
            String fahrschueler;

            try{
                //Check Fahrschueler
                String sql = "SELECT Schueler.Email FROM Schueler WHERE ? = Schueler.rowId";
                Map<String, Object> e = getStringObjectMap(fahrschuelerid, sql);
                fahrschueler = e.get("Email").toString();
            }catch(SQLException e){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            String fahrschule;
            try{
                //Get and check Fahrschule
                String sql = "SELECT Fahrzeug.Fahrschule FROM Fahrzeug WHERE ? = Fahrzeug.rowId";
                Map<String, Object> e = getStringObjectMap(fahrzeugid, sql);
                fahrschule = e.get("Fahrschule").toString();
            }catch(SQLException e){
                return Response.status(Response.Status.NOT_FOUND).build();
            }


            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement;

            //Fahrstunde speichern
            String sql = "INSERT INTO Fahrstunde (Schueler, Fahrlehrer, Fahrschule, Dauer, Typ, Preis) VALUES (?, ?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, fahrschueler);
            preparedStatement.setString(2, fahrlehrer);
            preparedStatement.setString(3, fahrschule);
            preparedStatement.setString(4, dauer);
            preparedStatement.setString(5, typ);
            preparedStatement.setString(6, preis);

            preparedStatement.executeUpdate();
            Long id = preparedStatement.getGeneratedKeys().getLong(1);
            preparedStatement.closeOnCompletion();

            return Response.status(Response.Status.CREATED).header("Location",
                    "fahrstunden/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        } catch (SQLException e){
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response getFahrstunde(String dauer, String fahrlehrer) throws SQLException{
        String sql = "SELECT Fahrschule.ROWID AS fahrschulid, Schueler.ROWID AS schuelerid, Fahrstunde.Dauer, Fahrstunde.Preis, Fahrstunde.Typ\n" +
                "FROM Fahrschule, Fahrstunde, Schueler\n" +
                "WHERE Fahrschule.Email=Fahrstunde.Fahrschule\n" +
                "AND Schueler.Email=Fahrstunde.Schueler\n" +
                "AND (Fahrstunde.Dauer >= ? OR ? IS NULL);";

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, dauer);
        preparedStatement.setObject(2, dauer);
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
        System.out.println(entities);

        List<Fahrstunde> fahrstunde = new ArrayList<>();

        entities.forEach(e -> {
            Fahrstunde tempFahrstunde = new Fahrstunde();

            tempFahrstunde.setFahrschuelerid(Integer.valueOf(e.get("schuelerid").toString()));
            tempFahrstunde.setTyp(e.get("Typ").toString());
            tempFahrstunde.setDauer(Integer.valueOf(e.get("Dauer").toString()));
            tempFahrstunde.setPreis(Double.valueOf(e.get("Preis").toString()));
            tempFahrstunde.setFahrschuleid(Integer.valueOf(e.get("fahrschulid").toString()));

            fahrstunde.add(tempFahrstunde);
        });
        preparedStatement.closeOnCompletion();

        return Response.status(Response.Status.OK).entity(fahrstunde).build();
    }

    private boolean getStringObjectMap(String fahrschuleEmail, String sql, String admin) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement2 = connection.prepareStatement(sql);
        preparedStatement2.setString(1, fahrschuleEmail);
        preparedStatement2.setString(2, admin);
        ResultSet resultSet = preparedStatement2.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        String[] strings = null;
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount();i++) {
            String name = metaData.getColumnName(i);
            list.add(name);
        }

        strings = list.toArray(String[]::new);
        List<Map<String, Object>> entities = resultSetToList(strings, resultSet);
        return entities.isEmpty();
    }

    private Map<String, Object> getStringObjectMap(String rowId, String sql) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement2 = connection.prepareStatement(sql);
        preparedStatement2.setString(1, rowId);
        ResultSet resultSet = preparedStatement2.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        String[] strings = null;
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount();i++) {
            String name = metaData.getColumnName(i);
            list.add(name);
        }

        strings = list.toArray(String[]::new);
        List<Map<String, Object>> entities = resultSetToList(strings, resultSet);
        return entities.get(0);
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
}
