package com.RubenDavid.proyectoTFG;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.util.Random;

public class VillagerEvents implements Listener {

    static MisionAsignada misionAsignada; //Guardamos como atributo statico la mision asignada, para poder manipularlo en los distintos eventos


    //GENERAR UN ARRAY DE MISIONES ASIGNADAS PARA PODER BORRAR UNA MISION POR UUID DEL JUGADOR, PARA PODER TENER VARIAS MISIONESASIGNADAS ACTIVAS A LA VEZ

    private ConexionMySQL conexion;

    public VillagerEvents(ConexionMySQL conexion) {
        this.conexion = conexion;
    }

    @EventHandler
    public void CrearMisiones(PlayerInteractAtEntityEvent event) {

        Entity villager = event.getRightClicked();
        if (!(villager instanceof Villager)) { //Si no hacemos click sobre un villager, salimos del metodo
            return;
        }
        if (event.getPlayer().isSneaking()) { //Si estamos agachados, salimos del metodo
            return;
        }

        if (misionAsignada == null){
            int rnd = new Random().nextInt(4); //Se hace un random del 0 al 3, que sera la id de la mision
            MisionPlantilla misionAleatoria = DatosCompartidos.plantillas.get(rnd);
            //Asignamos la mision aleatoria elegida al Arraylist de mision asignada, para mantenerla trakeada.
            misionAsignada = new MisionAsignada(misionAleatoria.getId(), misionAleatoria.getDescripcion(), misionAleatoria.getMaterial(),
                    misionAleatoria.getCriatura(), misionAleatoria.getCantidadTotal(), event.getPlayer().getUniqueId(),
                    event.getRightClicked().getUniqueId(), event.getRightClicked().getCustomName());

            DatosCompartidos.misionesAsignadas.add(misionAsignada);
            event.getPlayer().sendMessage(misionAsignada.getDescripcion());

            SQLPlayerData.crearMisionAsignada(conexion.getConnection(), event.getPlayer().getName(), misionAsignada);

            World world = villager.getWorld(); //Guardamos la ubicacion en el mundo de el aldeano (coordenadas)
            villager.setGlowing(true);

            event.getPlayer().sendMessage("Ultima mision asignada: " + misionAsignada.getId() + ".");
        }

        //Generar una recompensa
        if (misionAsignada.getCantidadActual() == misionAsignada.getCantidadTotal()){
            //Si el aldeano clickado y el aldeano de la mision son el mismo, damos la recompensa y liberamos la opcion de coger una nueva mision
            if (event.getRightClicked().getUniqueId() == misionAsignada.getVillager()){
                event.getPlayer().sendMessage("Muchas gracias, " + event.getPlayer().getDisplayName() + ", aqui esta tu recompensa ");

                if (misionAsignada.getId() != 3){
                    //(BORRAR OBJETOS) Creamos un itemstack con los datos del tipo de objeto y la cantidad requerida por la mision, para luego eliminarlo del inventario
                    ItemStack objetoMisionAEliminar = new ItemStack(misionAsignada.getMaterial(), misionAsignada.getCantidadTotal());
                    event.getPlayer().getInventory().removeItem(objetoMisionAEliminar);
                }

                //Generar array de recompensas
                Material[] arrayRecompensas = new Material[]{Material.BLAZE_ROD,Material.ENDER_PEARL,Material.NETHER_WART,Material.POTION,Material.EMERALD,Material.GOLD_INGOT,Material.EXPERIENCE_BOTTLE,Material.IRON_INGOT};
                int recompensaRandom = new Random().nextInt(arrayRecompensas.length);

                villager.getWorld().dropItemNaturally(villager.getLocation(), new ItemStack(arrayRecompensas[recompensaRandom], 1));
                villager.setGlowing(false);
                misionAsignada = null;
            }
        }
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
    public void RecogerObjeto(EntityPickupItemEvent event) {
        //Controlador de que estamos recogiendo el objeto correcto
        if(event.getEntity() instanceof Player) {
        Player player = (Player) event.getEntity();

            switch (misionAsignada.getId()) {
                case 0: //Recoger margaritas
                    if (event.getItem().getItemStack().getType() == Material.OXEYE_DAISY) {
                        if (misionAsignada.getCantidadActual() != misionAsignada.getCantidadTotal()) {
                            misionAsignada.setCantidadActual(misionAsignada.getCantidadActual() + 1);
                            player.sendMessage("Margaritas " + misionAsignada.getCantidadActual() +"/"+ misionAsignada.getCantidadTotal());
                        }
                        //Si la cantidad es identica, sale un mensaje de mision completada
                        if (misionAsignada.getCantidadActual() == misionAsignada.getCantidadTotal()){
                            player.sendMessage("Mision Completada, debo regresar a por mi recompensa, me la entregara "+ misionAsignada.getNombreVillager());
                        }
                    }
                    break;
                case 1: //Talar un arbol
                    if (event.getItem().getItemStack().getType() == Material.OAK_LOG) {
                        if (misionAsignada.getCantidadActual() != misionAsignada.getCantidadTotal()) {
                            misionAsignada.setCantidadActual(misionAsignada.getCantidadActual() + 1);
                            player.sendMessage("Madera " + misionAsignada.getCantidadActual() +"/"+ misionAsignada.getCantidadTotal());
                        }
                        //Si la cantidad es identica, sale un mensaje de mision completada
                        if (misionAsignada.getCantidadActual() == misionAsignada.getCantidadTotal()){
                            player.sendMessage("Mision Completada, debo regresar a por mi recompensa, me la entregara "+ misionAsignada.getNombreVillager());
                        }
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void FabricarObjetos(CraftItemEvent event){
        Player player = (Player) event.getWhoClicked(); //Guardamos al jugador que ha hecho click sobre la mesa de trabajo

        CraftingInventory inv = (CraftingInventory) event.getInventory();

        switch (misionAsignada.getId()){
            case 2: //Fabricar mesa de trabajo
                if (inv.contains(Material.CRAFTING_TABLE)) { //Si el resultado del crafteo es una mesa de Trabajo
                    if (misionAsignada.getCantidadActual() != misionAsignada.getCantidadTotal()) {
                        misionAsignada.setCantidadActual(misionAsignada.getCantidadActual() + 1);
                        player.sendMessage("Mesa de trabajo " + misionAsignada.getCantidadActual() +"/"+ misionAsignada.getCantidadTotal());
                    }
                    //Si la cantidad es identica, sale un mensaje de mision completada
                    if (misionAsignada.getCantidadActual() == misionAsignada.getCantidadTotal()){
                        player.sendMessage("Mision Completada, debo regresar a por mi recompensa, me la entregara "+ misionAsignada.getNombreVillager());
                    }
                }
                break;
        }
    }

    @EventHandler
    public void MatarCriatura(EntityDeathEvent event) {
        Bukkit.broadcastMessage("Mision asignada actual: " + misionAsignada.getDescripcion());
        Bukkit.broadcastMessage("La mision fue asignada por "+ misionAsignada.getNombreVillager());
        switch (misionAsignada.getId()){
            case 3:
                Chicken chicken = (Chicken) event.getEntity();
                Player asesino = chicken.getKiller();

                //Verificamos que la gallina muere a manos de un jugador y no por elementos externos
                if (asesino == null) {
                    return;
                }

                //Cada vez que el jugador mata una gallina, se aumenta en uno la cantidad actual, hasta llegar a la cantidad total
                if (misionAsignada.getCantidadActual() != misionAsignada.getCantidadTotal()){
                    misionAsignada.setCantidadActual(misionAsignada.getCantidadActual()+1);
                    chicken.getKiller().sendMessage("Pollos " + misionAsignada.getCantidadActual() +"/"+ misionAsignada.getCantidadTotal());
                }

                //Si la cantidad es identica, sale un mensaje de mision completada
                if (misionAsignada.getCantidadActual() == misionAsignada.getCantidadTotal()){
                    event.getEntity().getKiller().sendMessage("Mision Completada, debo regresar a por mi recompensa, me la entregara "+ misionAsignada.getNombreVillager());
                }
                break;
        }
    }

    //Si aparece un aldeano, lo hara con un nombre (NameTag) sobre su cabeza, para poder identificarlo.
    //Para este proceso usaremos dos Strings con nombres y apellidos, que juntaremos luego en el nombre
    @EventHandler
    public void PuestaDeNombres(EntitySpawnEvent event){
        if (event.getEntity() instanceof Villager){

            String[] nombres = {"Ruben", "David", "Ismael", "Roberto", "Alejandro", "Daniel", "Fran", "Manuel", "Miguel"};
            String[] apellidos={"Gómez", "Porras", "Castro", "Garcia", "Tajuelo", "López", "Arellano", "Anso", "Fernandez", "Pires", "Garcia"};

            //A la creacion de un aldeano, identificamos a dicho aldeano y le aplicamos un nombre.
            Villager villager =(Villager)event.getEntity();

            //Seleccionaremos ahora uno de los nombres guardados en el Array
            int nombreRandom = new Random().nextInt(nombres.length);
            String nombre = nombres[nombreRandom]; //Seleccionamos uno de los nombres

            //Y hacemos lo mismo con los apellidos
            int apellidoRandom = new Random().nextInt(apellidos.length);
            String apellido = apellidos[apellidoRandom];

            //Una vez formado el nombre y el apellido, se la aplicaremos al aldeano y haremos visible su nombre
            String nombreAldeano = nombre + " " + apellido;
            villager.setCustomName(nombreAldeano);
            villager.setCustomNameVisible(true);
        }
    }
}
