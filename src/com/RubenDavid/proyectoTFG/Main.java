package com.RubenDavid.proyectoTFG;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Main extends JavaPlugin implements Listener {

    private Connection conexion;

    @Override
    public void onEnable() {
        super.onEnable();

        //Creacion de las plantillas de misiones
        DatosCompartidos.plantillas.add(new MisionPlantilla(0, "Nuestro botanico necesita materiales, recoge tres margaritas", Material.OXEYE_DAISY, null, 3));
        DatosCompartidos.plantillas.add(new MisionPlantilla(1, "La poblacion va en aumento, tala un roble para poder hacer una cabaña", Material.OAK_LOG, null, 3));
        DatosCompartidos.plantillas.add(new MisionPlantilla(2, "Necesitamos mas artesanos, fabrica una mesa de trabajo", Material.CRAFTING_TABLE, null, 1));
        DatosCompartidos.plantillas.add(new MisionPlantilla(3, "Necesitamos alimento, sacrifica a un pollo", null, EntityType.CHICKEN, 1));

        try {
            this.conexion = DriverManager.getConnection("jdbc:sqlite:plugins/villagererrands.db");
            iniciarBaseDatos();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new VillagerEvents(conexion), this);

        //Actua cuando se ejecuta en comando /mision
        getCommand("mision").setExecutor(new Comando(this));
    }


    public Connection getSqliteConnection(){
        return this.conexion;
    }

    private void iniciarBaseDatos() {
        try {
            Statement stmt = getSqliteConnection().createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS `jugador` (\n" +
                    " `uuid` varchar(36) NOT NULL,\n" +
                    " `nombre` varchar(48) NOT NULL,\n" +
                    " `mision` int(2) NOT NULL\n" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS `mision_activa` (\n" +
                    " `id` int(11) NOT NULL,\n" +
                    " `descripcion` varchar(100) NOT NULL,\n" +
                    " `cantidad_total` int(2) NOT NULL,\n" +
                    " `uuid_jugador` varchar(36) NOT NULL,\n" +
                    " `nombre_jugador` varchar(48) NOT NULL,\n" +
                    " `uuid_aldeano` varchar(36) NOT NULL,\n" +
                    " `nombre_aldeano` varchar(21) NOT NULL,\n" +
                    " `cantidad_actual` int(2) NOT NULL\n" +
                    ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player jugador = event.getPlayer();
        Bukkit.broadcastMessage("Hola " + event.getPlayer().getDisplayName());
        if(!SQLPlayerData.jugadorExiste(getSqliteConnection(),jugador.getUniqueId())) {
            SQLPlayerData.crearJugador(getSqliteConnection(), jugador.getUniqueId(), jugador.getName());
        }
    }

    /*
     * ALT ENTER -> Ayuda, importar cosas
     * CTRL SPACE -> Autocompletado
     * ALT INSERT -> Generar código/Sobreescribir métodos
     * CTRL ALT L -> Poner codigo bonito
     * CTRL Q -> Ver documentacion donde tengas el cursor
     * CTRL P -> Ver nombre de parámetros de métodos/constructores
     * CTRL Click -> Navegar
     */
}
