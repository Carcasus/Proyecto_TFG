package com.RubenDavid.proyectoTFG;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;


public class MisionAsignada extends MisionPlantilla {

    private UUID player;
    private UUID villager;
    private int cantidadActual;

    public MisionAsignada(int id, String descripcion, Material material, EntityType criatura, int cantidadTotal, UUID player, UUID villager) {
        super(id, descripcion, material, criatura, cantidadTotal);
        this.cantidadActual = 0;
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

    public int getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(int cantidadActual) {
        this.cantidadActual = cantidadActual;
    }
}
