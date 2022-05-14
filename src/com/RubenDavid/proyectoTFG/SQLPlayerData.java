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
        } catch (SQLException e){}

        return false;
    }

    public static void crearJugador(Connection connection, UUID uuid, String nombre) {
        try {
            if (!jugadorExiste(connection,uuid)) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO jugador VALUE (?,?,?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, nombre);
                statement.setInt(3, 0); //Mision
                statement.executeUpdate();
                }
        } catch (SQLException e) {
        }
    }

    public static int getMision(Connection connection, UUID uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM jugador WHERE (uuid=?)");
            statement.setString(1, uuid.toString());
            ResultSet resultado = statement.executeQuery();

            if (resultado.next()) {
                int mision = resultado.getInt("Mision");
                return mision;
            }
        } catch (SQLException e){}
        return 0;
    }

    /*public static void setMision(Connection connection, UUID uuid, int mision) {
        int id = MisionPlantilla.class.getI;
        String descripcion;
        Material material;
        EntityType criatura;
        int cantidadTotal;

        MisionPlantilla misionPlantilla = new MisionPlantilla(id, descripcion, )
        MisionAsignada misionAsignada = new MisionAsignada(uuid,  );
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE jugador SET mision=? WHERE (uuid=?)");
            statement.setInt(, mision);
            statement.setString(1, uuid.toString());
            statement.executeQuery();

        } catch (SQLException e){}
    }*/



}