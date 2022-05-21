package com.RubenDavid.proyectoTFG;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLPlayerData {

    public static boolean jugadorExiste(Connection connection, UUID uuid) {
        try {

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM jugador WHERE (uuid=?)");
            statement.setString(1, uuid.toString());
            ResultSet resultado = statement.executeQuery();

            if (resultado.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void crearJugador(Connection connection, UUID uuid, String nombre) {
        try {
            if (!jugadorExiste(connection, uuid)) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO jugador VALUE (?,?,?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, nombre);
                statement.setInt(3, 0); //Mision
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void crearMisionAsignada(Connection connection, String nombre, MisionAsignada misionAsignada) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM mision_activa WHERE (uuid_jugador=?)");
            statement.setString(1, misionAsignada.getPlayer().toString());
            ResultSet resultado = statement.executeQuery();

            if (resultado.next()) {
                actualizarMisionAsignada(connection, misionAsignada);
            } else {
                int id = misionAsignada.getId();
                String descripcion = misionAsignada.getDescripcion();
                int cantidadTotal = misionAsignada.getCantidadTotal();
                String nombreVillager = misionAsignada.getNombreVillager();
                int cantidadActual = misionAsignada.getCantidadActual();


                try {
                    statement = connection.prepareStatement("INSERT INTO mision_activa VALUE (?,?,?,?,?,?,?,?)");
                    statement.setInt(1, id);
                    statement.setString(2, descripcion);
                    statement.setInt(3, cantidadTotal);
                    statement.setString(4, misionAsignada.getPlayer().toString());
                    statement.setString(5, nombre);
                    statement.setString(6, misionAsignada.getVillager().toString());
                    statement.setString(7, nombreVillager);
                    statement.setInt(8, cantidadActual);

                    statement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void actualizarMisionAsignada(Connection connection, MisionAsignada misionAsignada) {

        String descripcion = misionAsignada.getDescripcion();
        int cantidadTotal = misionAsignada.getCantidadTotal();
        String nombreVillager = misionAsignada.getNombreVillager();
        int cantidadActual = misionAsignada.getCantidadActual();



        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE mision_activa SET descripcion=?, cantidad_total=?, uuid_aldeano=?, " +
                    "nombre_aldeano=?, cantidad_actual=? WHERE (uuid_jugador=?)");
            statement.setString(1, descripcion);
            statement.setInt(2, cantidadTotal);
            statement.setString(3, misionAsignada.getVillager().toString());
            statement.setString(4, nombreVillager);
            statement.setInt(5, cantidadActual);
            statement.setString(6, misionAsignada.getPlayer().toString());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Suma una mision a mision total en la base de datos
    public static void sumarMision(Connection connection, MisionAsignada misionAsignada) {
        int numMision = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT mision FROM jugador WHERE (uuid=?)");
            statement.setString(1, misionAsignada.getPlayer().toString());
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                numMision = resultado.getInt("mision");
            }

            int suma= numMision +1;
            statement = connection.prepareStatement("UPDATE jugador SET Mision=? WHERE (uuid=?)");
            statement.setInt(1, suma);
            statement.setString(2, misionAsignada.getPlayer().toString());
            statement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Metodo utilizado para el comando mision
    public static String getMision(Connection connection, UUID uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT descripcion FROM mision_activa WHERE (uuid_jugador=?)");
            statement.setString(1, uuid.toString());
            ResultSet resultado = statement.executeQuery();

            if (resultado.next() && resultado.getString("descripcion") != "") {
                String mision = resultado.getString("descripcion");
                return mision;
            }else{
                return "No tienes asignada ninguna mision";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No tienes asignada ninguna mision";
    }
}