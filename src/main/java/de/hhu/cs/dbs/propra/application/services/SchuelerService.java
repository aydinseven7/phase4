package de.hhu.cs.dbs.propra.application.services;

import de.hhu.cs.dbs.propra.domain.model.Fahrstunde;
import de.hhu.cs.dbs.propra.domain.model.Schueler;

import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchuelerService {
    private DataSource dataSource;

    public SchuelerService(DataSource dataSource) {
        this.dataSource = dataSource;
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

    public Response addFahrschuelerToUebung(String uebungid, String fahrschueler) throws SQLException{
        System.out.println(uebungid);
        System.out.println(fahrschueler);
        try{
            String uebung;
            try{
                //Check Uebung
                String sql = "SELECT theoretische_Uebung.id FROM theoretische_Uebung WHERE ? = theoretische_Uebung.rowId";
                Map<String, Object> e = getStringObjectMap(uebungid, sql);
                uebung = e.get("id").toString();
            }catch(SQLException e){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement;

            //Uebungsteilnahme speichern
            String sql = "INSERT INTO Schueler_Belegt_Theoretische_Uebung (Schueler, theoretische_Uebung) VALUES (?, ?)";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(2, uebung);
            preparedStatement.setString(1, fahrschueler);

            preparedStatement.executeUpdate();
            Long id = preparedStatement.getGeneratedKeys().getLong(1);
            preparedStatement.closeOnCompletion();

            return Response.status(Response.Status.CREATED).header("Location",
                    "theorieuebungen/" + uebungid + "/fahrschueler/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        } catch (SQLException e){
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
