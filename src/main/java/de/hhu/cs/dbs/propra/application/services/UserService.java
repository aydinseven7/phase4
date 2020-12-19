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


public class UserService {
    private DataSource dataSource;

    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Response createFahrschueler(String email, String password, String vorname, String nachname, String geschlecht, Integer addressId) throws SQLException {
        Connection connection = dataSource.getConnection();
        // User speichern
        try {
            connection.setAutoCommit(false);
            addUser(email, password, vorname, nachname, connection);

            if(checkAdressExists(addressId)){
                Map<String, Object> entity = new HashMap<>();
                entity.put("message", "Die angegebene Adresse existiert nicht!");
                return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
            }
            PreparedStatement preparedStatement;

            //Fahrschueler speichern
            String sql2 = "INSERT INTO Schueler (Email, Geschlecht, Adresse) VALUES (?, ?, ?)";

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, geschlecht);
            preparedStatement.setObject(3, addressId);

            preparedStatement.executeUpdate();

            Long id = preparedStatement.getGeneratedKeys().getLong(1);

            connection.commit();

            return Response.status(Response.Status.CREATED).header("Location",
                    "fahrschueler/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        }
        catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            Map<String, Object> entity = new HashMap<>();
            entity.put("message", "Die angegebenen Werte sind fehlerhaft!" + e.getLocalizedMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        }
        finally {
            connection.close();
        }
    }

    public Response createAdmin(String email, String password, String vorname, String nachname, String telefonnummer) throws SQLException {
        Connection connection = dataSource.getConnection();
        // User speichern
        try {
            connection.setAutoCommit(false);
            addUser(email, password, vorname, nachname, connection);

            PreparedStatement preparedStatement;

            //Admin speichern
            String sql2 = "INSERT INTO Admin (Email, Telefonnummer) VALUES (?, ?)";

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, telefonnummer);

            preparedStatement.executeUpdate();

            Long id = preparedStatement.getGeneratedKeys().getLong(1);
            connection.commit();

            return Response.status(Response.Status.CREATED).header("Location",
                    "admins/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        }catch (SQLException e){
            e.printStackTrace();
            connection.rollback();
            Map<String, Object> entity = new HashMap<>();
            entity.put("message", "Die angegebenen Werte sind fehlerhaft!" + e.getLocalizedMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        }
        finally {
            connection.close();
        }
    }
    public Response createFahrlehrer(String email, String password, String vorname, String nachname, String lizenzdatum) throws SQLException {
        Connection connection = dataSource.getConnection();
        // User speichern
        try {
            connection.setAutoCommit(false);
            addUser(email, password, vorname, nachname, connection);

            PreparedStatement preparedStatement;

            //Fahrlehrer speichern
            String sql2 = "INSERT INTO Fahrlehrer (Email, Fahrlehrerlizenz) VALUES (?, ?)";

            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, lizenzdatum);

            preparedStatement.executeUpdate();

            Long id = preparedStatement.getGeneratedKeys().getLong(1);
            connection.commit();

            return Response.status(Response.Status.CREATED).header("Location",
                    "fahrlehrer/" + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8)).build();
        }
        catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            Map<String, Object> entity = new HashMap<>();
            entity.put("message", "Die angegebenen Werte sind fehlerhaft!" + e.getLocalizedMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        }
        finally {
            connection.close();
        }
    }

    private void addUser(String email, String password, String vorname, String nachname, Connection connection) throws SQLException {
        String sql = "INSERT INTO Nutzer (Email, Passwort, Vorname, Nachname) VALUES (?, ?, ? , ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, email);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, vorname);
        preparedStatement.setString(4, nachname);

        preparedStatement.executeUpdate();
    }

    private boolean checkAdressExists(Integer adressId) throws SQLException {
        String sql = "SELECT ID FROM Adresse WHERE ID = ?";

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, adressId);
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

        try {
            String sql = "SELECT Fahrlehrer.RowId, Fahrlehrer.Fahrlehrerlizenz, Nutzer.Email, Nutzer.Passwort, Nutzer.Vorname, Nutzer.Nachname FROM Fahrlehrer,Nutzer WHERE Fahrlehrer.Email = Nutzer.Email AND (Fahrlehrer.Fahrlehrerlizenz >= ? OR ? IS NULL) AND (lower(Nutzer.Nachname) = lower(?) OR ? IS NULL)";

            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql);
            preparedStatement2.setString(1, lizenzdatum);
            preparedStatement2.setString(2, lizenzdatum);
            preparedStatement2.setString(3, nachname);
            preparedStatement2.setString(4, nachname);
            ResultSet resultSet = preparedStatement2.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            String[] strings = null;
            ArrayList<String> list = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
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
            preparedStatement2.closeOnCompletion();

            return Response.status(Response.Status.OK).entity(fahrlehrer).build();
        } catch(SQLException e){
            e.printStackTrace();
            Map<String, Object> entity = new HashMap<>();
            entity.put("message", "Die angegebenen Werte sind fehlerhaft!" + e.getLocalizedMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        }
    }

    public Response getFahrschule(String bezeichnung, String klasse) throws SQLException {
        try {
            String sql = "SELECT DISTINCT Fahrschule.ROWID AS fahrschuleid, Adresse.ROWID AS adresseid, Fahrschule.Email, Fahrschule.Website, Fahrschule.Bezeichnung\n" +
                    "FROM Fahrschule, Fahrschule_besitzt_Adresse, Adresse, Fahrzeug\n" +
                    "WHERE Fahrschule.Email = Fahrschule_besitzt_Adresse.Fahrschule\n" +
                    "AND Adresse.id = Fahrschule_besitzt_Adresse.Adresse\n" +
                    "AND (lower(Fahrschule.Bezeichnung) = lower(?) OR ? IS NULL)\n" +
                    "AND ((Fahrzeug.Fahrschule = Fahrschule.Email AND lower(Fahrzeug.Fahrzeugklasse) = lower(?)) OR ? IS NULL);";

            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql);
            preparedStatement2.setString(1, bezeichnung);
            preparedStatement2.setString(2, bezeichnung);
            preparedStatement2.setString(3, klasse);
            preparedStatement2.setString(4, klasse);
            ResultSet resultSet = preparedStatement2.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            preparedStatement2.closeOnCompletion();

            String[] strings = null;
            ArrayList<String> list = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i);
                list.add(name);
            }

            strings = list.toArray(String[]::new);
            List<Map<String, Object>> entities = resultSetToList(strings, resultSet);

        List<Fahrschule> fahrschule = new ArrayList<>();

        entities.forEach(e -> {
            Fahrschule tempFahrschule = new Fahrschule();

            tempFahrschule.setFahrschuleid(Integer.valueOf(e.get("fahrschuleid").toString()));
            tempFahrschule.setAdresseid(Integer.valueOf(e.get("adresseid").toString()));
            tempFahrschule.setEmail(e.get("Email").toString());
            tempFahrschule.setBezeichnung(e.get("Bezeichnung").toString());
            tempFahrschule.setWebsite(e.get("Website").toString());

            fahrschule.add(tempFahrschule);
        });
            return Response.status(Response.Status.OK).entity(fahrschule).build();
        } catch(SQLException e){
            e.printStackTrace();
            Map<String, Object> entity = new HashMap<>();
            entity.put("message", "Die angegebenen Werte sind fehlerhaft!" + e.getLocalizedMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        }
    }

    public Response getFahrzeuge(String kennzeichen, String erst) throws SQLException{
        try {
            String sql = "SELECT Fahrzeug.RowId AS fahrzeugId, Fahrzeug.Kennzeichen, Fahrzeug.'HU-Eintrag', Fahrzeug.Erstzulassung, Fahrschule.ROWID AS fahrschuleId FROM Fahrzeug, Fahrschule WHERE Fahrzeug.Fahrschule = Fahrschule.Email AND (strftime('%Y-%m-%d', Fahrzeug.Erstzulassung) >= ? OR ? IS NULL) AND (instr(Fahrzeug.Kennzeichen, ?) > 0 OR ? IS NULL)";

            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql);
            preparedStatement2.setString(1, erst);
            preparedStatement2.setString(2, erst);
            preparedStatement2.setString(3, kennzeichen);
            preparedStatement2.setString(4, kennzeichen);
            ResultSet resultSet = preparedStatement2.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            String[] strings = null;
            ArrayList<String> list = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i);
                list.add(name);
            }

            strings = list.toArray(String[]::new);
            List<Map<String, Object>> entities = resultSetToList(strings, resultSet);

            List<Fahrzeug> fahrzeuge = new ArrayList<>();

            entities.forEach(e -> {
                Fahrzeug tempFahrzeug = new Fahrzeug();

                tempFahrzeug.setFahrschuleid(Integer.valueOf(e.get("fahrschuleId").toString()));
                tempFahrzeug.setFahrzeugid(Integer.valueOf(e.get("fahrzeugId").toString()));
                tempFahrzeug.setKennzeichen(e.get("Kennzeichen").toString());
                tempFahrzeug.setHudatum(e.get("HU-Eintrag").toString());
                tempFahrzeug.setErstzulassung(e.get("Erstzulassung").toString());

                fahrzeuge.add(tempFahrzeug);
            });
            preparedStatement2.closeOnCompletion();

            return Response.status(Response.Status.OK).entity(fahrzeuge).build();
        } catch(SQLException e){
            e.printStackTrace();
            Map<String, Object> entity = new HashMap<>();
            entity.put("message", "Die angegebenen Werte sind fehlerhaft!" + e.getLocalizedMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        }
    }

    public Response getAdressen(String hausnummer) throws SQLException{
        try {
            String sql = "SELECT *, Adresse.ROWID FROM Adresse WHERE instr(Adresse.Hausnummer,?) OR ? IS NULL";

            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql);
            preparedStatement2.setString(1, hausnummer);
            preparedStatement2.setString(2, hausnummer);
            ResultSet resultSet = preparedStatement2.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            String[] strings = null;
            ArrayList<String> list = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i);
                list.add(name);
            }

            strings = list.toArray(String[]::new);
            List<Map<String, Object>> entities = resultSetToList(strings, resultSet);
            System.out.println(entities);

            List<Adresse> adressen = new ArrayList<>();

            entities.forEach(e -> {
                Adresse tempAdresse = new Adresse();

                tempAdresse.setAdresseid(Integer.valueOf(e.get("id").toString()));
                tempAdresse.setStadt(e.get("Stadt").toString());
                tempAdresse.setStrasse(e.get("Strasse").toString());
                tempAdresse.setPlz(e.get("PLZ").toString());
                tempAdresse.setHausnummer(e.get("Hausnummer").toString());

                adressen.add(tempAdresse);
            });
            preparedStatement2.closeOnCompletion();

            return Response.status(Response.Status.OK).entity(adressen).build();
        } catch(SQLException e){
            e.printStackTrace();
            Map<String, Object> entity = new HashMap<>();
            entity.put("message", "Die angegebenen Werte sind fehlerhaft!" + e.getLocalizedMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        }
    }

    public Response getUebung(String themabezeichnung, Integer dauer, Boolean verpflichtend, Integer fahrschuleid) {
        try {
            String sql = "SELECT theoretische_Uebung.ROWID AS uebungID, Fahrschule.ROWID AS fahrschulid, theoretische_Uebung.Thema, theoretische_Uebung.Pflicht, theoretische_Uebung.Dauer\n" +
                    "FROM Fahrschule, theoretische_Uebung\n" +
                    "WHERE Fahrschule.Email = theoretische_Uebung.Fahrschule\n" +
                    "AND (theoretische_Uebung.Dauer >= ? OR ? IS NULL)\n" +
                    "AND (lower(theoretische_Uebung.Thema) = lower(?) OR ? IS NULL)\n" +
                    "AND (theoretische_Uebung.Pflicht = ? OR ? IS NULL)" +
                    "AND (Fahrschule.ROWID = ? OR ? IS NULL)\n";
            Integer pflicht = 0;
            if(verpflichtend != null) {
                if(verpflichtend) { pflicht = 1;}
                if(!verpflichtend) { pflicht = 0;}}
            else{
                pflicht = null;
            }

            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql);
            preparedStatement2.setObject(1, dauer);
            preparedStatement2.setObject(2, dauer);
            preparedStatement2.setString(3, themabezeichnung);
            preparedStatement2.setString(4, themabezeichnung);
            preparedStatement2.setObject(5, pflicht);
            preparedStatement2.setObject(6, pflicht);
            preparedStatement2.setObject(7, fahrschuleid);
            preparedStatement2.setObject(8, fahrschuleid);
            ResultSet resultSet = preparedStatement2.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            String[] strings = null;
            ArrayList<String> list = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i);
                list.add(name);
            }

            strings = list.toArray(String[]::new);
            List<Map<String, Object>> entities = resultSetToList(strings, resultSet);

            List<Uebung> uebungen = new ArrayList<>();

            entities.forEach(e -> {
                Uebung tempUebung = new Uebung();

                tempUebung.setTheorieuebungid(Integer.valueOf(e.get("uebungID").toString()));
                tempUebung.setFahrschuleid(Integer.valueOf(e.get("fahrschulid").toString()));
                tempUebung.setVerpflichtend(Boolean.valueOf(e.get("Pflicht").toString().equals("1")));
                tempUebung.setDauer(Integer.valueOf(e.get("Dauer").toString()));
                tempUebung.setThemabezeichnung(e.get("Thema").toString());

                uebungen.add(tempUebung);
            });
            preparedStatement2.closeOnCompletion();

            return Response.status(Response.Status.OK).entity(uebungen).build();
        } catch(SQLException e){
            e.printStackTrace();
            Map<String, Object> entity = new HashMap<>();
            entity.put("message", "Die angegebenen Werte sind fehlerhaft!" + e.getLocalizedMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
        }
    }
}