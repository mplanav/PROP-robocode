package follow;

import java.awt.Color;
import java.awt.Graphics2D;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

/**
 * Classe principal del robot ByteForce, que controla les diferents accions i
 * estats en què es troba el robot.
 *
 * @author Daniel Aagaard i Marc Plana
 */
public class ByteForce extends TeamRobot {
    public Estat estat;

    /**
     * Mètode principal que inicialitza l'estat inicial del robot. Es defineix a
     * quin estat començarà el robot.
     */
    @Override
    public void run() {
        estat = new Estat_CL(this);

        while (true) {
            estat.run();
        }
    }
    
    /**
     * Mètode que es crida quan el radar detecta un altre robot. Es delega la
     * resposta a l'estat actual del robot.
     *
     * @param event L'esdeveniment de detecció d'un robot.
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        //estat.onScannedRobot(event);
    }
    
    /**
     * Mètode que es crida quan rebem un missatge de un membre de l'equip.
     *
     * @param event L'esdeveniment de missatge.
     */
    @Override
    public void onMessageReceived(MessageEvent event) {
        estat.onMessageReceived(event);
    }
    
    /**
     * Mètode que es crida per pintar el robot.
     *
     * @param g Grafics del robot.
     */
    @Override
    public void onPaint(Graphics2D g) {
        estat.onPaint(g);
    }
    
    /**
     * Mètode que canvia l'estat actual del robot.
     *
     * @param e El nou estat que s'ha d'assignar.
     */
    public void setEstat(Estat e) {
        this.estat = e;
    }
}
