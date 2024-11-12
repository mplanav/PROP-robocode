package timidin;

import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

/**
 * Estat en què el robot detecta un enemic i calcula la cantonada mes allunyada
 * d'aquest.
 *
 * @author Daniel Aagaard i Marc Plana
 */
public class Estat_LF extends Estat {

    private ByteForce robot;

    /**
     * Constructor de la classe Estat_LF.
     *
     * @param robot El robot controlat.
     */
    public Estat_LF(ByteForce robot) {
        this.robot = robot;
    }

    /**
     * Mètode que executa l'estat de vigilància, on el radar gira contínuament.
     */
    @Override
    void run() {
        robot.setTurnRadarRight(10);
        robot.execute();
    }

    /**
     * Mètode que calcula la distancia a partir de les 2 posicions dels robots.
     *
     * @param x1 Posicio X del robot.
     * @param y1 Posicio Y del robot.
     * @param x2 Posicio X del robot enemic.
     * @param y2 Posicio Y del robot enemic.
     */
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Mètode que es crida quan es detecta un altre robot. El robot calcula la
     * cantonada mes allunyada d'aquest enemic.
     *
     * @param event L'esdeveniment de detecció d'un robot.
     */
    @Override
    void onScannedRobot(ScannedRobotEvent event) {

        double angle = Math.toRadians(robot.getHeading() + event.getBearing());
        double posX = robot.getX() + Math.sin(angle) * event.getDistance();
        double posY = robot.getY() + Math.cos(angle) * event.getDistance();

        double width = robot.getBattleFieldWidth();
        double height = robot.getBattleFieldHeight();

        // Cada distancia a l'enemic amb la cantonada i la respectiva cantonada
        double[] dist1 = {distance(posX, posY, 0, 0), 0, 0};
        double[] dist2 = {distance(posX, posY, 0, height), 0, height};
        double[] dist3 = {distance(posX, posY, width, 0), width, 0};
        double[] dist4 = {distance(posX, posY, width, height), width, height};

        // Calcul de la cantonada mes allunyada
        double posMVx = 20;
        double posMVy = 20;

        double maxDistance = Math.max(Math.max(dist3[0], dist4[0]), Math.max(dist1[0], dist2[0]));
        if (maxDistance == dist2[0]) {
            posMVx = 20;
            posMVy = height - 20;
        } else if (maxDistance == dist3[0]) {
            posMVx = width - 20;
            posMVy = 20;
        } else if (maxDistance == dist4[0]) {
            posMVx = width - 20;
            posMVy = height - 20;
        }

        // Canviem l'estat del robot
        Estat_MV estatMV = new Estat_MV(robot, posMVx, posMVy);
        robot.setEstat(estatMV);
    }

    /**
     * Mètode que es crida quan es xoquem amb un obstacle.
     *
     * @param event L'esdeveniment de xoc amb un robot.
     */
    @Override
    void onHitRobot(HitRobotEvent event) {
        
    }
}
