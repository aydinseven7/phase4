package de.hhu.cs.dbs.propra.application.services;

import de.hhu.cs.dbs.propra.domain.model.Pruefung;

import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService{
    private DataSource dataSource;

    public AdminService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Response createFahrschule(String email, String website, String bezeichnung, String addressId, String adminEmail) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            if(checkAdressExists(addressId)){
                return Response.status(Response.Status.BAD_REQUEST).entity("Diese Adresse existiert nicht!").build();
            }

            connection.setAutoCommit(false);
            PreparedStatement preparedStatement;

            //Fahrschule speichern
            String sql = "INSERT INTO Fahrschule (Email, Website, Bezeichnung, Admin) VALUES (?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, website);
            preparedStatement.setString(3, bezeichnung);
            preparedStatement.setString(4, adminEmail);

            preparedStatement.executeUpdate();
            Long id = preparedStatement.getGeneratedKeys().getLong(1);
            preparedStatement.closeOnCompletion();

            //Adresse speichern
            String sql2 = "INSERT INTO Fahrschule_besitzt_Adresse (Fahrschule, Adresse) VALUES (?, ?)";

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, addressId);

            preparedStatement.executeUpdate();
            //preparedStatement.closeOnCompletion();

            return Response.status(Response.Status.CREATED).header("Location",
                    "fahrschulen/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        } catch (SQLException e){
            e.printStackTrace();
            connection.rollback();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        finally {
            connection.close();
        }
    }

    public Response createFahrzeug(String fahrschuleid, String fahrzeugklasse, String kennzeichen, String hudatum, String erst, String admin) throws SQLException {
        try {
            String fahrschuleEmail;
            String klasse;

            try{
                //Check Fahrschule
                String sql = "SELECT Fahrschule.Email FROM Fahrschule WHERE ? = Fahrschule.rowId";
                Map<String, Object> e = getStringObjectMap(fahrschuleid, sql);
                fahrschuleEmail = e.get("Email").toString();

                //Check if Admin at Fahrschule
                sql = "SELECT Fahrschule.Email FROM Fahrschule WHERE ? = Fahrschule.Email AND Fahrschule.Admin = ?";
                if(getStringObjectMap(fahrschuleEmail, sql, admin)){
                    return Response.status(Response.Status.NOT_FOUND).build();
                }

                //Check Fahrzeugklasse
                sql = "SELECT Fahrzeugklasse.Bezeichnung FROM Fahrzeugklasse WHERE ? = Fahrzeugklasse.rowId";
                Map<String, Object> e1 = getStringObjectMap(fahrzeugklasse, sql);
                klasse = e1.get("Bezeichnung").toString();
                System.out.println(klasse);

            }catch(SQLException e){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement;

            //Fahrzeug speichern
            String sql = "INSERT INTO Fahrzeug (Fahrzeugklasse, Fahrschule, Kennzeichen, 'HU-Eintrag', Erstzulassung) VALUES (?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, klasse);
            preparedStatement.setString(2, fahrschuleEmail);
            preparedStatement.setString(3, kennzeichen);
            preparedStatement.setString(4, hudatum);
            preparedStatement.setString(5, erst);

            preparedStatement.executeUpdate();
            Long id = preparedStatement.getGeneratedKeys().getLong(1);
            preparedStatement.closeOnCompletion();


            return Response.status(Response.Status.CREATED).header("Location",
                    "fahrzeug/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }



    public Response createUebung(String fahrschuleid, String themabezeichnung, String dauer, String verpflichtend, String admin) throws SQLException{
        try{
            String fahrschuleEmail;

            try{
                //Check Fahrschule
                String sql = "SELECT Fahrschule.Email FROM Fahrschule WHERE ? = Fahrschule.rowId";
                Map<String, Object> e = getStringObjectMap(fahrschuleid, sql);
                fahrschuleEmail = e.get("Email").toString();

                //Check if Admin at Fahrschule
                sql = "SELECT Fahrschule.Email FROM Fahrschule WHERE ? = Fahrschule.Email AND Fahrschule.Admin = ?";
                if(getStringObjectMap(fahrschuleEmail, sql, admin)){
                    return Response.status(Response.Status.NOT_FOUND).build();
                }

            }catch(SQLException e){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement;

            //Uebung speichern
            String sql = "INSERT INTO theoretische_Uebung (Pflicht, Dauer, Thema, Fahrschule) VALUES (?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, verpflichtend);
            preparedStatement.setString(2, dauer);
            preparedStatement.setString(3, themabezeichnung);
            preparedStatement.setString(4, fahrschuleEmail);

            preparedStatement.executeUpdate();
            Long id = preparedStatement.getGeneratedKeys().getLong(1);
            preparedStatement.closeOnCompletion();


            return Response.status(Response.Status.CREATED).header("Location",
                    "theorieuebungen/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        } catch (SQLException e){
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response createPruefung(String fahrschuelerid, String gebuehr, String typ, String ergebnis, String admin) throws SQLException{
        Connection connection = dataSource.getConnection();
        try{
            String fahrschueler;

            try{
                //Check Fahrschueler
                String sql = "SELECT Schueler.Email FROM Schueler WHERE ? = Schueler.rowId";
                Map<String, Object> e = getStringObjectMap(fahrschuelerid, sql);
                fahrschueler = e.get("Email").toString();
                if(e.isEmpty()){
                    return Response.status(Response.Status.BAD_REQUEST).entity("Diese id existiert nicht").build();
                }
            }catch(SQLException e){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            connection.setAutoCommit(false);
            PreparedStatement preparedStatement;

            //Pruefung speichern
            String sql = "INSERT INTO Pruefung (Typ, Teilnahmegebuehr) VALUES (?, ?)";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(2, gebuehr);
            preparedStatement.setString(1, typ);

            preparedStatement.executeUpdate();
            Long id = preparedStatement.getGeneratedKeys().getLong(1);
            //preparedStatement.closeOnCompletion();

            //Pruefungsteilnahme speichern
            String sql2 = "INSERT INTO Schueler_Belegt_Pruefung (Schueler, Pruefung, Erfolgreich) VALUES (?, ?, ?)";

            System.out.println(ergebnis);
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1, fahrschueler);
            preparedStatement.setString(2, id.toString());
            preparedStatement.setString(3, ergebnis);

            preparedStatement.executeUpdate();
            //preparedStatement.closeOnCompletion();
            connection.commit();


            return Response.status(Response.Status.CREATED).header("Location",
                    "theorieuebungen/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        } catch (SQLException e){
            e.printStackTrace();
            connection.rollback();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        finally {
            connection.close();
        }
    }

    public Response getPruefung(Integer fahrschuelerid, Double gebuehr, Boolean typ, Boolean ergebnis) throws SQLException{
        String sql = "SELECT Schueler_Belegt_Pruefung.Schueler, Schueler_Belegt_Pruefung.Erfolgreich, Pruefung.Teilnahmegebuehr, Pruefung.Typ, Schueler.ROWID AS schuelerID\n" +
                "FROM Schueler_Belegt_Pruefung, Pruefung, Schueler\n" +
                "WHERE Pruefung.id = Schueler_Belegt_Pruefung.Pruefung\n" +
                "AND Schueler.Email = Schueler_Belegt_Pruefung.Schueler\n" +
                "AND (Pruefung.Typ = ? OR ? IS NULL)\n" +
                "AND (schuelerID = ? OR ? IS NULL)\n" +
                "AND (Pruefung.Teilnahmegebuehr >= ? OR ? IS NULL)\n" +
                "AND (Schueler_Belegt_Pruefung.Erfolgreich = ? OR ? IS NULL);";

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement2 = connection.prepareStatement(sql);
        preparedStatement2.setObject(1, typ);
        preparedStatement2.setObject(2, typ);
        preparedStatement2.setObject(3, fahrschuelerid);
        preparedStatement2.setObject(4, fahrschuelerid);
        preparedStatement2.setObject(5, gebuehr);
        preparedStatement2.setObject(6, gebuehr);
        preparedStatement2.setObject(7, ergebnis);
        preparedStatement2.setObject(8, ergebnis);
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
        System.out.println(entities);

        List<Pruefung> pruefung = new ArrayList<>();

        entities.forEach(e -> {
            Pruefung tempPruefung = new Pruefung();

            tempPruefung.setFahrschuelerid(Integer.valueOf(e.get("schuelerID").toString()));
            tempPruefung.setTyp(e.get("Typ").toString().equals("0"));
            tempPruefung.setErgebnis(e.get("Erfolgreich").toString().equals("0"));
            tempPruefung.setGebuehr(Double.valueOf(e.get("Teilnahmegebuehr").toString()));

            pruefung.add(tempPruefung);
        });
        preparedStatement2.closeOnCompletion();

        return Response.status(Response.Status.OK).entity(pruefung).build();
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
