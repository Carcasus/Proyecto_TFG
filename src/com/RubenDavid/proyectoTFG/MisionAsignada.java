package com.RubenDavid.proyectoTFG;

import java.util.UUID;


public class MisionAsignada extends MisionPlantilla {

    private UUID player;
    private UUID villager;
    private String nombreVillager;
    private int cantidadActual;


    public MisionAsignada(MisionPlantilla misionPlantilla, UUID player, UUID villager, String nombreVillager) {
        super(misionPlantilla.getId(), misionPlantilla.getDescripcion(), misionPlantilla.getMaterial(), misionPlantilla.getCriatura(), misionPlantilla.getCantidadTotal());
        this.cantidadActual = 0;
        this.player = player;
        this.villager = villager;
        this.nombreVillager = nombreVillager;
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

    public String getNombreVillager() {
        return nombreVillager;
    }

    public void setNombreVillager(String nombreVillager) {
        this.nombreVillager = nombreVillager;
    }

    public int getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(int cantidadActual) {
        this.cantidadActual = cantidadActual;
    }
}
