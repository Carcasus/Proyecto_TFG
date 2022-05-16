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


public class Main extends JavaPlugin implements Listener {

    private ConexionMySQL conexion;

    @Override
    public void onEnable() {
        super.onEnable();

        //Creacion de las plantillas de misiones
        DatosCompartidos.plantillas.add(new MisionPlantilla(0, "Nuestro botanico necesita materiales, recoge tres margaritas", Material.OXEYE_DAISY, null, 3));
        DatosCompartidos.plantillas.add(new MisionPlantilla(1, "La poblacion va en aumento, tala un arbol para poder hacer una cabaña", Material.OAK_LOG, null, 3));
        DatosCompartidos.plantillas.add(new MisionPlantilla(2, "Necesitamos mas artesanos, fabrica una mesa de trabajo", Material.CRAFTING_TABLE, null, 1));
        DatosCompartidos.plantillas.add(new MisionPlantilla(3, "Necesitamos alimento, sacrifica a un pollo", null, EntityType.CHICKEN, 1));

        this.conexion = new ConexionMySQL("localhost",3306,"villagererrandsdb","root","");

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new VillagerEvents(conexion), this);

        //registerCommands();

    }



    /*public void registerCommands(){
    this.getCommand("mision").setExecutor(new Comando(this));
    }*/

    public Connection getMySQL(){
        return this.conexion.getConnection();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player jugador = event.getPlayer();
        Bukkit.broadcastMessage("Hola " + event.getPlayer().getDisplayName());
        if(!SQLPlayerData.jugadorExiste(getMySQL(),jugador.getUniqueId())) {
            SQLPlayerData.crearJugador(getMySQL(), jugador.getUniqueId(), jugador.getName());
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
