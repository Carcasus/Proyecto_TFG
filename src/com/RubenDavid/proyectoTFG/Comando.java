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

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player jugador = (Player) sender;
        int misiones = SQLPlayerData.getMision(plugin.getMySQL(), jugador.getUniqueId());
        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aActualmente tienes asignada la mision: &7"+misiones));

        return true;
    }
}
