package com.RubenDavid.proyectoTFG;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private Main plugin;
    public PlayerListener(Main plugin){
        this.plugin=plugin;
    }

    @EventHandler
    public void alEntrar(PlayerJoinEvent event){
        Player jugador = event.getPlayer();
        if(!SQLPlayerData.jugadorExiste(plugin.getMySQL(),jugador.getUniqueId())) {
            SQLPlayerData.crearJugador(plugin.getMySQL(), jugador.getUniqueId(), jugador.getName());
        }
    }

}
