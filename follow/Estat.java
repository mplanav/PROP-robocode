package follow;

import java.awt.Graphics2D;
import java.io.Serializable;
import robocode.MessageEvent;
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
     * Mètode que es crida quan rebem un missatge de un membre de l'equip.
     *
     * @param event L'esdeveniment de missatge.
     */
    abstract void onMessageReceived(MessageEvent event);

    /**
     * Mètode que es crida per pintar el robot.
     *
     * @param g Grafics del robot.
     */
    abstract void onPaint(Graphics2D g);

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

    /**
     * Classe que es guarda la posicio X i la posicio Y.
     *
     */
    public static class Posicio implements Serializable {

        private static final long serialVersionUID = 1L;
        public double x, y;

        public Posicio(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    
    /**
     * Classe que es guarda la posició i el nom del robot.
     *
     */
    public static class Seguiment implements Serializable {

        private static final long serialVersionUID = 1L;
        String nom;
        Posicio posicio;

        public Seguiment(String nom, Posicio posicio) {
            this.nom = nom;
            this.posicio = posicio;
        }
    }
}
