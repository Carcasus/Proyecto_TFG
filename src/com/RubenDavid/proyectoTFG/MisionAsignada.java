package com.RubenDavid.proyectoTFG;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;


public class MisionAsignada extends MisionPlantilla {

    UUID player;
    UUID villager;

    public MisionAsignada(int id, String descripcion, Material material, EntityType criatura, int cantidadTotal, UUID player, UUID villager) {
        super(id, descripcion, material, criatura, cantidadTotal);
        this.player = player;
        this.villager = villager;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public UUID getVillager() {
        return villager;
    }

    public void setVillager(UUID villager) {
        this.villager = villager;
    }
}
