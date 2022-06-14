package com.RubenDavid.proyectoTFG;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;


public class VillagerEvents implements Listener {

    ArrayList<MisionAsignada> listaDeMisionesAsignadas = new ArrayList<>();
    MisionPlantilla misionAleatoria;

    private Connection conexion;

    public VillagerEvents(Connection conexion) {
        this.conexion = conexion;
    }

    @EventHandler
    public void CrearMisiones(PlayerInteractAtEntityEvent event) throws SQLException {

        PreparedStatement statement = conexion.prepareStatement("SELECT * FROM mision_activa WHERE (uuid_jugador=?)");
        statement.setString(1, event.getPlayer().getUniqueId().toString());
        ResultSet resultado = statement.executeQuery();

        Entity villager = event.getRightClicked();

        if (!(villager instanceof Villager)) { //Si no hacemos click sobre un villager, salimos del metodo
            return;
        }
        if (event.getPlayer().isSneaking()) { //Si estamos agachados, salimos del metodo

            return;
        }

        if (!resultado.next()) { //Si el jugador no ha hecho antes ninguna mision o no tiene mision activa

            int rnd = new Random().nextInt(4); //Se hace un random del 0 al 3, que sera la id de la mision
            misionAleatoria = DatosCompartidos.plantillas.get(rnd);
            //Asignamos la mision aleatoria elegida al Arraylist de mision asignada, para mantenerla trakeada.

            listaDeMisionesAsignadas.add(new MisionAsignada(misionAleatoria, event.getPlayer().getUniqueId(),
                    event.getRightClicked().getUniqueId(), event.getRightClicked().getCustomName()));


            //Escribimos por pantalla para ese jugador la descripcion de la mision, siempre y cuando tenga una mision en la lista
            for (int i = 0; i < listaDeMisionesAsignadas.size(); i++) {

                if (listaDeMisionesAsignadas.get(i).getPlayer().equals(event.getPlayer().getUniqueId())) {

                    event.getPlayer().sendMessage(listaDeMisionesAsignadas.get(i).getDescripcion());
                    SQLPlayerData.crearMisionAsignada(conexion, event.getPlayer().getName(), listaDeMisionesAsignadas.get(i));

                    villager.setGlowing(true); //Ponemos el marcado al aldeano

                }
            }
        }

        //Generar una recompensa, para ello nos aseguramos que el jugador tiene asignada una mision con todas las cantidades acordes
        for (int i = 0; i < listaDeMisionesAsignadas.size(); i++) {
            if (listaDeMisionesAsignadas.get(i).getPlayer().equals(event.getPlayer().getUniqueId())
                    && (listaDeMisionesAsignadas.get(i).getCantidadActual() == listaDeMisionesAsignadas.get(i).getCantidadTotal())) {

                //Si el aldeano clickado y el aldeano de la mision son el mismo, damos la recompensa y liberamos la opcion de coger una nueva mision
                if (listaDeMisionesAsignadas.get(i).getVillager().equals(event.getRightClicked().getUniqueId())) {
                    event.getPlayer().sendMessage("Muchas gracias, " + event.getPlayer().getDisplayName() + ", aqui esta tu recompensa ");

                    //Si no es mision de matar criaturas, sera una mision que implique recoger algun objeto, con lo cual
                    //deberemos aplicar el borrado de objetos del jugador.
                    //(BORRAR OBJETOS) Creamos un itemstack con los datos del tipo de objeto y la cantidad requerida por la mision,
                    // para luego eliminarlo del inventario
                    if (listaDeMisionesAsignadas.get(i).getMaterial() != null) {
                        ItemStack objetoMisionAEliminar = new ItemStack(listaDeMisionesAsignadas.get(i).getMaterial(),
                                listaDeMisionesAsignadas.get(i).getCantidadTotal());
                        event.getPlayer().getInventory().removeItem(objetoMisionAEliminar);
                    }

                    //Generar array de recompensas
                    Material[] arrayRecompensas = new Material[]{Material.BLAZE_ROD, Material.ENDER_PEARL, Material.NETHER_WART,
                            Material.POTION, Material.EMERALD, Material.GOLD_INGOT, Material.EXPERIENCE_BOTTLE, Material.IRON_INGOT};
                    int recompensaRandom = new Random().nextInt(arrayRecompensas.length);

                    SQLPlayerData.sumarMision(conexion, listaDeMisionesAsignadas.get(i));

                    villager.getWorld().dropItemNaturally(villager.getLocation(), new ItemStack(arrayRecompensas[recompensaRandom], 1));
                    villager.setGlowing(false);
                    listaDeMisionesAsignadas.remove(i); //retiramos del array la fila actual

                    statement = conexion.prepareStatement("DELETE FROM mision_activa WHERE (uuid_jugador=?)");
                    statement.setString(1, event.getPlayer().getUniqueId().toString());
                    statement.executeUpdate();
                }
            }
        }
    }

    @EventHandler
    public void cancelarMercadoSiDePie(InventoryOpenEvent event) { //Bloqueamos el acceso al mercado de los villager si no estamos agachados
        if (event.getPlayer() instanceof Player player) {
            if (!player.isSneaking() && event.getInventory().getType() == InventoryType.MERCHANT) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void RecogerObjeto(EntityPickupItemEvent event) {
        //Controlador de que estamos recogiendo el objeto correcto
        if (event.getEntity() instanceof Player player) {
            for (int i = 0; i < listaDeMisionesAsignadas.size(); i++) {
                switch (listaDeMisionesAsignadas.get(i).getId()) {
                    case 0: //Recoger margaritas, si es el tipo correcto, sumamos +1 al contador
                        if (event.getItem().getItemStack().getType() == Material.OXEYE_DAISY) {
                            if (listaDeMisionesAsignadas.get(i).getCantidadActual() != listaDeMisionesAsignadas.get(i).getCantidadTotal()) {

                                listaDeMisionesAsignadas.get(i).setCantidadActual((listaDeMisionesAsignadas.get(i).getCantidadActual() + 1));
                                player.sendMessage("Margaritas " + listaDeMisionesAsignadas.get(i).getCantidadActual() +
                                        "/" + listaDeMisionesAsignadas.get(i).getCantidadTotal());
                            }

                            //Si la cantidad es identica, sale un mensaje de mision completada
                            if (listaDeMisionesAsignadas.get(i).getCantidadActual() == listaDeMisionesAsignadas.get(i).getCantidadTotal()) {
                                player.sendMessage("Mision Completada, debo regresar a por mi recompensa, me la entregara " +
                                        listaDeMisionesAsignadas.get(i).getNombreVillager());
                            }
                        }
                        break;
                    case 1: //Talar un arbol, si es el tipo correcto, sumamos +1 al contador
                        if (event.getItem().getItemStack().getType() == Material.OAK_LOG) {
                            if (listaDeMisionesAsignadas.get(i).getCantidadActual() != listaDeMisionesAsignadas.get(i).getCantidadTotal()) {

                                listaDeMisionesAsignadas.get(i).setCantidadActual((listaDeMisionesAsignadas.get(i).getCantidadActual() + 1));
                                player.sendMessage("Madera " + listaDeMisionesAsignadas.get(i).getCantidadActual() +
                                        "/" + listaDeMisionesAsignadas.get(i).getCantidadTotal());
                            }
                            //Si la cantidad es identica, sale un mensaje de mision completada
                            if (listaDeMisionesAsignadas.get(i).getCantidadActual() == listaDeMisionesAsignadas.get(i).getCantidadTotal()) {
                                player.sendMessage("Mision Completada, debo regresar a por mi recompensa, me la entregara " +
                                        listaDeMisionesAsignadas.get(i).getNombreVillager());
                            }
                        }
                        break;
                }
            }
        }
    }

    @EventHandler
    public void FabricarObjetos(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked(); //Guardamos al jugador que ha hecho click sobre la mesa de trabajo

        CraftingInventory inv = (CraftingInventory) event.getInventory();

        for (int i = 0; i < listaDeMisionesAsignadas.size(); i++) {
            switch (listaDeMisionesAsignadas.get(i).getId()) {
                case 2: //Fabricar mesa de trabajo, si es el tipo correcto, sumamos +1 al contador
                    if (inv.contains(Material.CRAFTING_TABLE)) {
                        if (listaDeMisionesAsignadas.get(i).getCantidadActual() != listaDeMisionesAsignadas.get(i).getCantidadTotal()) {

                            listaDeMisionesAsignadas.get(i).setCantidadActual((listaDeMisionesAsignadas.get(i).getCantidadActual() + 1));
                            player.sendMessage("Mesa de trabajo " + listaDeMisionesAsignadas.get(i).getCantidadActual() +
                                    "/" + listaDeMisionesAsignadas.get(i).getCantidadTotal());
                        }

                        //Si la cantidad es identica, sale un mensaje de mision completada
                        if (listaDeMisionesAsignadas.get(i).getCantidadActual() == listaDeMisionesAsignadas.get(i).getCantidadTotal()) {
                            player.sendMessage("Mision Completada, debo regresar a por mi recompensa, me la entregara " +
                                    listaDeMisionesAsignadas.get(i).getNombreVillager());
                        }
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void MatarCriatura(EntityDeathEvent event) {
        for (int i = 0; i < listaDeMisionesAsignadas.size(); i++) {
            switch (listaDeMisionesAsignadas.get(i).getId()) {
                case 3:
                    if (!(event.getEntity() instanceof Chicken)) {
                        return;
                    }
                    Chicken chicken = (Chicken) event.getEntity();
                    Player asesino = chicken.getKiller();

                    //Verificamos que la gallina muere a manos de un jugador y no por elementos externos
                    if (asesino == null) {
                        return;
                    }

                    //Cada vez que el jugador mata una gallina, se aumenta en uno la cantidad actual, hasta llegar a la cantidad total
                    if (listaDeMisionesAsignadas.get(i).getCantidadActual() != listaDeMisionesAsignadas.get(i).getCantidadTotal()) {

                        listaDeMisionesAsignadas.get(i).setCantidadActual((listaDeMisionesAsignadas.get(i).getCantidadActual() + 1));
                        chicken.getKiller().sendMessage("Pollos " + listaDeMisionesAsignadas.get(i).getCantidadActual() +
                                "/" + listaDeMisionesAsignadas.get(i).getCantidadTotal());
                    }

                    //Si la cantidad es identica, sale un mensaje de mision completada
                    if (listaDeMisionesAsignadas.get(i).getCantidadActual() == listaDeMisionesAsignadas.get(i).getCantidadTotal()) {
                        event.getEntity().getKiller().sendMessage("Mision Completada, debo regresar a por mi recompensa, me la entregara " +
                                listaDeMisionesAsignadas.get(i).getNombreVillager());
                    }
                    break;
            }
        }
    }

    //Si aparece un aldeano, lo hara con un nombre (NameTag) sobre su cabeza, para poder identificarlo.
    //Para este proceso usaremos dos Strings con nombres y apellidos, que juntaremos luego en el nombre
    @EventHandler
    public void PuestaDeNombres(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Villager) {

            String[] nombres = {"Ruben", "David", "Ismael", "Roberto", "Alejandro", "Daniel", "Fran", "Manuel", "Miguel", "Adrian", "Victor", "Sergio"};
            String[] apellidos = {"Gómez", "Porras", "Castro", "Garcia", "Tajuelo", "López", "Arellano", "Anso", "Fernandez", "Pires", "Garcia", "Casas"};

            //A la creacion de un aldeano, identificamos a dicho aldeano y le aplicamos un nombre.
            Villager villager = (Villager) event.getEntity();

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

