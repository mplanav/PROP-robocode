package timidin;

import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

/**
 * Classe abstracta que defineix l'estructura dels diferents estats del robot.
 * Tots els estats hauran d'implementar els mètodes abstractes.
 *
 * @author Daniel Aagaard i Marc Plana
 */
abstract class Estat {

    /**
     * Mètode que executa la lògica pròpia de cada estat.
     */
    abstract void run();

    /**
     * Mètode que es crida quan es detecta un altre robot.
     *
     * @param event L'esdeveniment de detecció d'un robot.
     */
    abstract void onScannedRobot(ScannedRobotEvent event);
    
    /**
     * Mètode que es crida quan es xoquem amb un obstacle.
     *
     * @param event L'esdeveniment de xoc amb un robot.
     */
    abstract void onHitRobot(HitRobotEvent event);

    /**
     * Mètode per normalitzar un angle, perquè estigui entre -180 i 180 graus.
     *
     * @param angle L'angle a normalitzar.
     * @return L'angle normalitzat.
     */
    protected double normalizeAngle(double angle) {
        while (angle > 180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }
}
