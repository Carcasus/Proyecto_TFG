package com.RubenDavid.proyectoTFG;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionMySQL {

    private Connection connection;

    private String host;
    private int puerto;
    private String database;
    private String usuario;
    private String contrasena;

    public ConexionMySQL(String host, int puerto, String database, String usuario, String contrasena) {
        this.host = host;
        this.puerto = puerto;
        this.database = database;
        this.usuario = usuario;
        this.contrasena = contrasena;


        try{
            synchronized (this){

                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://"+this.host+":"+this.puerto+"/"+this.database,this.usuario,this.contrasena);

                Statement stmt = this.connection.createStatement();

                //If que creara la base de datos si no se encuentra creada ya
                String query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'villagererrandsdb";
                if (stmt.executeUpdate(query) != 1){
                    stmt.execute("create database villagererrandsdb");

                    stmt.execute("CREATE TABLE `jugador` (\n" +
                            " `uuid` varchar(36) NOT NULL,\n" +
                            " `nombre` varchar(48) NOT NULL,\n" +
                            " `mision` int(2) NOT NULL\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

                    stmt.execute("CREATE TABLE `mision_activa` (\n" +
                            " `id` int(11) NOT NULL,\n" +
                            " `descripcion` varchar(100) NOT NULL,\n" +
                            " `cantidad_total` int(2) NOT NULL,\n" +
                            " `uuid_jugador` varchar(36) NOT NULL,\n" +
                            " `nombre_jugador` varchar(48) NOT NULL,\n" +
                            " `uuid_aldeano` varchar(36) NOT NULL,\n" +
                            " `nombre_aldeano` varchar(21) NOT NULL,\n" +
                            " `cantidad_actual` int(2) NOT NULL\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
                }

                if (connection != null && !connection.isClosed()){
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&4[&cMiPluginSQL&4]&a Conectado con MySQL"));
                } else{
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[&cMiPluginSQL&4]&c Error al conectar con MySQL"));
                }
            }
        }catch(SQLException e){
        }catch(ClassNotFoundException e){
        }

    }

    public Connection getConnection() {
        return connection;
    }
}
