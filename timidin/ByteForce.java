package timidin;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

/**
 * Classe principal del robot ByteForce, que controla les diferents accions i
 * estats en què es troba el robot.
 *
 * @author Daniel Aagaard i Marc Plana
 */
public class ByteForce extends AdvancedRobot {

    public Estat estat;

    /**
     * Mètode principal que inicialitza l'estat inicial del robot. Es defineix a
     * quin estat començarà el robot.
     */
    @Override
    public void run() {
        estat = new Estat_LF(this);

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
        //Crida a cada onScannedRobot que es troba definit en cada Estat.
        estat.onScannedRobot(event);
    }

    /**
     * Mètode que es crida quan es xoquem amb un obstacle.
     *
     * @param event L'esdeveniment de xoc amb un robot.
     */
    @Override
    public void onHitRobot(HitRobotEvent event) {
        estat.onHitRobot(event);
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
