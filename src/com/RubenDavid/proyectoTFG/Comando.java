package com.RubenDavid.proyectoTFG;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comando implements CommandExecutor {

    private Main plugin;
    public Comando(Main plugin){
        this.plugin=plugin;
    }

    @Override
    public boolean onCommand(CommandSender comandsender, Command command, String label, String[] args) {
        if (!(comandsender instanceof Player)) {
            return true;
        }
        Player jugador = (Player) comandsender;
        //Coge la mision asignada del jugador que ha escrito el comando
        String misiones = SQLPlayerData.getMision(plugin.getMySQL(), jugador.getUniqueId());
        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aMision asignada: &7"+misiones));

        return true;
    }
}
