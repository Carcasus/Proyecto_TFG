package com.RubenDavid.proyectoTFG;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class MisionPlantilla {

    int id;
    String descripcion;
    Material material;
    EntityType criatura;
    int cantidadTotal;

    public MisionPlantilla(int id, String descripcion, Material material, EntityType criatura, int cantidadTotal) {
        this.id = id;
        this.descripcion = descripcion;
        this.material = material;
        this.criatura = criatura;
        this.cantidadTotal = cantidadTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public EntityType getCriatura() {
        return criatura;
    }

    public void setCriatura(EntityType criatura) {
        this.criatura = criatura;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(int cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }
}
