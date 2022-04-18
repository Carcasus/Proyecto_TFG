package com.RubenDavid.proyectoTFG;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class VillagerEvents implements Listener {

    @EventHandler
    public void CrearMisiones(PlayerInteractAtEntityEvent event) {

        Entity villager = event.getRightClicked();
        if (!(villager instanceof Villager)) { //Si no hacemos click sobre un villager, salimos del metodo
            return;
        }
        if (event.getPlayer().isSneaking()) { //Si estamos agachados, salimos del metodo actual (saltamos directamente al eventHandler de cancelacion)
            return;
        }

        int rnd = new Random().nextInt(4); //Se hace un random del 0 al 3, que sera la id de la mision
        MisionPlantilla misionAleatoria = DatosCompartidos.plantillas.get(rnd);
        //Asignamos la mision aleatoria elegida al Arraylist de mision asignada, para mantenerla trakeada.
        MisionAsignada misionAsignada = new MisionAsignada(misionAleatoria.id, misionAleatoria.descripcion, misionAleatoria.material,
                misionAleatoria.criatura, misionAleatoria.cantidadTotal, event.getPlayer().getUniqueId(),
                event.getRightClicked().getUniqueId());

        DatosCompartidos.misionesAsignadas.add(misionAsignada);
        event.getPlayer().sendMessage(misionAsignada.descripcion);

        World world = villager.getWorld(); //Guardamos la ubicacion en el mundo de el aldeano (coordenadas)
        //world.spawnParticle(Particle.GLOW, villager.getLocation().add(0, 0.5, 0), 50, 0.1, 0.1, 0.1, 0.1); //Spawneamos una particula sobre el aldeano
        villager.setGlowing(true);
    }

    @EventHandler
    public void cancelarMercadoSiDePie(InventoryOpenEvent event) { //Bloqueamos el acceso al mercado de los villager si no estamos agachados
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (!player.isSneaking() && event.getInventory().getType() == InventoryType.MERCHANT) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void RecogerObjeto(InventoryPickupItemEvent event) {
        //(Necesario revisar la clase del inventario)
        //Controlador de que estamos recogiendo el objeto correcto
        //Controlador de que aun no hemos llegado a la cantidad total requerida


        //Generar una recompensa
        //villager.getWorld().dropItemNaturally(villager.getLocation(), new ItemStack(Material.STICK, 1));
    }

    @EventHandler
    public void MatarCriatura(EntityDeathEvent event) {

    }


}
