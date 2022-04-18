package com.RubenDavid.proyectoTFG;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Main extends JavaPlugin implements Listener {



    @Override
    public void onEnable() {
        super.onEnable();

        //Creacion de las plantillas de misiones
        DatosCompartidos.plantillas.add(new MisionPlantilla(0, "Nuestro botanico necesita materiales, recoge tres margaritas", Material.OXEYE_DAISY, null, 3));
        DatosCompartidos.plantillas.add(new MisionPlantilla(1, "La poblacion va en aumento, tala un arbol para poder hacer una cabaña", Material.OAK_LOG, null, 3));
        DatosCompartidos.plantillas.add(new MisionPlantilla(2, "Necesitamos mas artesanos, fabrica una mesa de trabajo", Material.CRAFTING_TABLE, null, 3));
        DatosCompartidos.plantillas.add(new MisionPlantilla(3, "Necesitamos alimento, sacrifica a un pollo", null, EntityType.CHICKEN, 1));


        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new VillagerEvents(), this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Bukkit.broadcastMessage("Hola " + event.getPlayer().getDisplayName());
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