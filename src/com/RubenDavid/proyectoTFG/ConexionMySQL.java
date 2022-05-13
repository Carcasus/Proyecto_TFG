package com.RubenDavid.proyectoTFG;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
                if (connection != null && !connection.isClosed()){
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&4[&cMiPluginSQL&4]&c Error al conectar con MySQL"));
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://"+this.host+":"+this.puerto+"/"+this.database,this.usuario,this.contrasena);
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&4[&cMiPluginSQL&4]&a Conectado con MySQL"));
            }
        }catch(SQLException e){
        }catch(ClassNotFoundException e){
        }

    }

    public Connection getConnection() {
        return connection;
    }
}
