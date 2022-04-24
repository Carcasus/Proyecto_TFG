package com.RubenDavid.proyectoTFG;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class VillagerEvents implements Listener {

    static MisionAsignada misionAsignada; //Guardamos como atributo statico la mision asignada, para poder manipularlo en los distintos eventos


    //GENERAR UN ARRAY DE MISIONES ASIGNADAS PARA PODER BORRAR UNA MISION POR UUID DEL JUGADOR, PARA PODER TENER VARIAS MISIONESASIGNADAS ACTIVAS A LA VEZ


    @EventHandler
    public void CrearMisiones(PlayerInteractAtEntityEvent event) {

        Entity villager = event.getRightClicked();
        if (!(villager instanceof Villager)) { //Si no hacemos click sobre un villager, salimos del metodo
            return;
        }
        if (event.getPlayer().isSneaking()) { //Si estamos agachados, salimos del metodo
            return;
        }

        if (misionAsignada == null || misionAsignada.getId() != 3){
            int rnd = new Random().nextInt(4); //Se hace un random del 0 al 3, que sera la id de la mision
            MisionPlantilla misionAleatoria = DatosCompartidos.plantillas.get(rnd);
            //Asignamos la mision aleatoria elegida al Arraylist de mision asignada, para mantenerla trakeada.
            misionAsignada = new MisionAsignada(misionAleatoria.getId(), misionAleatoria.getDescripcion(), misionAleatoria.getMaterial(),
                    misionAleatoria.getCriatura(), misionAleatoria.getCantidadTotal(), event.getPlayer().getUniqueId(),
                    event.getRightClicked().getUniqueId(), event.getRightClicked().getCustomName());

            DatosCompartidos.misionesAsignadas.add(misionAsignada);
            event.getPlayer().sendMessage(misionAsignada.getDescripcion());

            World world = villager.getWorld(); //Guardamos la ubicacion en el mundo de el aldeano (coordenadas)
            villager.setGlowing(true);

            Bukkit.broadcastMessage("Ultima mision asignada: " + misionAsignada.getId() + ".\n La mision fue asignada por "+ misionAsignada.getNombreVillager());
        }

        //Generar una recompensa
        if (misionAsignada.getCantidadActual() == misionAsignada.getCantidadTotal()){
            //Si el aldeano clickado y el aldeano de la mision son el mismo, damos la recompensa y liberamos la opcion de coger una nueva mision
            if (event.getRightClicked().getUniqueId() == misionAsignada.getVillager()){
                event.getPlayer().sendMessage("Muchas gracias, " + event.getPlayer().getDisplayName() + ", aqui esta tu recompensa ");
                villager.getWorld().dropItemNaturally(villager.getLocation(), new ItemStack(Material.STICK, 1));
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
    public void RecogerObjeto(InventoryPickupItemEvent event) {
        //Controlador de que estamos recogiendo el objeto correcto

        switch (misionAsignada.getId()){
            case 0: //Recoger margaritas
                //event.getItem().getItemStack().getType(Material.OXEYE_DAISY);
                break;
            case 1: //Talar un arbol
                //event.getItem().getItemStack().getType(Material.OAK_LOG);
                break;
            case 2: //Fabricar mesa de trabajo
                break;
        }
    }

    @EventHandler
    public void MatarCriatura(EntityDeathEvent event) {
        Bukkit.broadcastMessage("Mision asignada actual: " + misionAsignada.getId());
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
            case 4:
                break;
        }
    }

    //Si aparece un aldeano, lo hara con un nombre (NameTag) sobre su cabeza, para poder identificarlo, el nombre sera unico.
    //Para este proceso desarrollamos un StringBuilder para seleccionar de forma aleatoria caracteres para el nombre cada vez que aparece un aldeano nuevo
    @EventHandler
    public void PuestaDeNombres(EntitySpawnEvent event){
        if (event.getEntity() instanceof Villager){
            //Herramientas que usaremos para poner los nombres
            String letrasMayusculas = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
            String letrasMinusculas = "abcdefghijklmnñopqrstuvwxyz";
            String[] apellidos={"Gómez", "Porras", "Castro", "Garcia", "Tajuelo", "López"};
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            char letraAleatoria;
            int longitudNombre = 7;

            //A la creacion de un aldeano, identificamos a dicho aldeano y le aplicamos un nombre.
            Villager villager =(Villager)event.getEntity();

            for (int i = 0; i <longitudNombre; i++){
                if (i == 0){ //Letra Mayuscula inicial del Nombre
                    int primeraLetra = random.nextInt(letrasMayusculas.length());
                    letraAleatoria = letrasMayusculas.charAt(primeraLetra);
                    sb.append(letraAleatoria);
                } //Resto de letras
                int restoLetras = random.nextInt(letrasMinusculas.length());
                letraAleatoria = letrasMinusculas.charAt(restoLetras);
                sb.append(letraAleatoria);
            }

            //Seleccionaremos ahora uno de los apellidos guardados en el Array
            int apellidoRandom = new Random().nextInt(apellidos.length);
            String apellido = apellidos[apellidoRandom]; //Seleccionamos uno de los apellidos

            //Una vez formada la string, se la aplicaremos al aldeano y haremos visible su nombre, añadimos tambien el apellido de la lista Array
            String nombreAldeano = sb.toString() + " " + apellido;
            villager.setCustomName(nombreAldeano);
            villager.setCustomNameVisible(true);
        }
    }


}
