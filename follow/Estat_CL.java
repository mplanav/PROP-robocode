package follow;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import java.util. *;

/**
 * Classe que representa l'estat d'escollir un lider del team, lider al que
 * seguirem després d'establir una jerarquia entre la resta de components basada
 * en el robot que tingui una distància inferior al robot de jerarquia superior
 * 
 * @author Daniel Aagaard i Marc Plana
 */
public class Estat_CL extends Estat {
    
   ByteForce robot;
   private HashMap<String, Double> distancies = new HashMap<>();
   private HashMap<String, Posicio> posicions = new HashMap<>();
   private static List<String> membres = new LinkedList<>();
   private static List<String> jerarquia = new LinkedList<>();
   private boolean ordenant = true;
   private boolean rebuts = false;
   private double distancia = 0;

   /**
    * Constructor de la classe Estat_MV.
    *
    * @param robot El robot controlat.
    */
    public Estat_CL(ByteForce robot) {
        this.robot = robot;
    }

    /**
     * Mètode que executa l'estat actual del robot. Escollim un líder aleatoriament
     * de la llista de membres de l'equip un cop mesclats amb el shuffle, posteriorment
     * establim una jerarquia segons les distàncies entre el robot que fem líder i 
     * la resta de robots, enviem missatges de les posicions on es troba cada escú
     * per tal de poder fer els càlculs pertinents i saber qui està més aprop de qui
     */
    @Override
    public void run() {
        if(membres.isEmpty())
        {
            //llista amb tots els membres de l'equip
            membres = Arrays.asList(robot.getTeammates());
        }   
        //mesclem aleatoriament la llista
        Collections.shuffle(membres);
        if(jerarquia.size() == 5) 
        {
            //si hem omplert la llista jerarquia vol dir que ja l'hem establerta
            //per tant canviem d'estat
            Estat_MV estatMV = new Estat_MV(robot);
            robot.setEstat(estatMV);
        }
        //si encara seguim en procés d'ordenació:
        else if(ordenant)
        {
            if(jerarquia.isEmpty()) distancia = Math.hypot(robot.getX(), robot.getY());
            Object[] missatge = {robot.getName(), distancia};
            distancies.put(robot.getName(), distancia);
            //Enviarem la posició a la resta de companys
            try{
                robot.broadcastMessage(missatge);
            } catch(IOException e) {
                e.getMessage();
            }
            //si ja hem rebut tots els missatges:
            if(rebuts)
            {
                if(!jerarquia.isEmpty())
                {
                    //per cada membre que ja hem afegit a la llista jeràrquia
                    //l'esborrem de la llista distàncies per agafar el següent
                    for (String membreJerarquia : jerarquia) distancies.remove(membreJerarquia);
                }
            }
            String proper = null;
            double distanciaMinima = Double.MAX_VALUE;
            
            for(Map.Entry<String, Double> valorEntrant : distancies.entrySet())
            {
                if(valorEntrant.getValue() < distanciaMinima)
                {
                    distanciaMinima = valorEntrant.getValue();
                    proper = valorEntrant.getKey();
                }
            }
            
            jerarquia.add(proper);
            distancies.clear();
            
            rebuts = false; 
            ordenant = false; //ja estan ordenats
        }
        //si la llista jerarquia actualment ja conté el nom del robot que es troba
        //executant llavors enviarem la nostra posició per tal que el següent que hagi
        //de seguir-nos la sàpiga, a partir d'aquí també hi calculem la distància al robot
        else if(jerarquia.contains(robot.getName()))
        {
            Object[] missatge = {robot.getName(), robot.getX(), robot.getY()};
            try {
                robot.broadcastMessage(missatge);
            } catch (IOException e) {
                e.getMessage();
            }
            ordenant = true;
        }
        //el que fem en cas contrari es calcular la distància del nostre anterior
        else if(!jerarquia.contains(robot.getName()))
        {
            if(posicions.size() == 1)
            {
                Map.Entry<String, Posicio> posicionsB = posicions.entrySet().iterator().next();
                String nom = posicionsB.getKey();
                Posicio pos= posicionsB.getValue();
                distancia = Math.hypot(robot.getX() - pos.x, robot.getY() - pos.y);
                ordenant = true;
            }
        }
    }

    @Override
    void onScannedRobot(ScannedRobotEvent event) {

    }

    
    /**
     * Mètode que s'executa en el moment que el robot reb un missatge. El que fem 
     * es recollir aquest missatge i el separem en el nom del que l'ha enviat i 
     * la posició on es troba aquest robot, a partir d'aquí calculem la distància
     * que hi tenim a aquest robot i l'afegim a la llista. En cas que la llista jerarquia 
     * ja contingui el nom de qui l'envia llavors el que farem es afegir a la hash de les posicions
     */
    
    @Override
    void onMessageReceived(MessageEvent event) {
        if(ordenant)
        {
            Object[] missatge = (Object[]) event.getMessage();
            String nom = (String) missatge[0];
            Posicio pos = (Posicio) missatge[1];
            double dist = Math.hypot(pos.x - robot.getX(), pos.y - robot.getY());
            distancies.put(nom, dist);
            if(distancies.size() == 5) rebuts = true;
        }
        else if(jerarquia.contains(robot.getName()))
        {
            Object[] missatge = (Object[]) event.getMessage();
            String nom = (String) missatge[0];
            Posicio pos = (Posicio) missatge[1];
            if(nom.equals(jerarquia.getLast())) posicions.put(nom, pos);
        }
}

    /**
     * Mètode que s'executa en el moment que ja tenim el líder escollit i li donem a paint
     * el que fa es encerclar el robot que està executant en aquest moment només en cas de ser 
     * el líder
     */
    
    @Override
    void onPaint(Graphics2D g) {
        if (!jerarquia.isEmpty() && jerarquia.get(0) == robot.getName()) {
            g.setColor(Color.yellow);
            int radi = 25;

            // Dibuixar una circumferencia centrada al lider
            g.drawOval((int) (robot.getX() - radi), (int) (robot.getY() - radi), radi * 2, radi * 2);
        }
    }
}