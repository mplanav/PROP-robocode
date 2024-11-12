package timidin;

import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

/**
 * Classe que representa l'estat de moviment vers un objectiu del robot. El
 * robot es desplaça cap a una posició específica (posMVx, posMVy) i oscil·la el
 * radar per detectar enemics en el camí.
 *
 * @author Daniel Aagaard i Marc Plana
 */
public class Estat_MV extends Estat {

    private ByteForce robot;
    private double posMVx;
    private double posMVy;
    private boolean radarOscillation = true;
    private boolean corner = false;

    /**
     * Constructor de la classe Estat_MV.
     *
     * @param robot El robot controlat.
     * @param posMVx La posició X de la destinació.
     * @param posMVy La posició Y de la destinació.
     */
    public Estat_MV(ByteForce robot, double posMVx, double posMVy) {
        this.robot = robot;
        this.posMVx = posMVx;
        this.posMVy = posMVy;
    }

    /**
     * Mètode que executa l'estat actual del robot. Es desplaça cap a la posició
     * establerta i ajusta el radar. També comprova si el robot ha arribat a la
     * cantonada.
     */
    @Override
    void run() {
        // Calcula l'angle pel desti
        double dirX = posMVx - robot.getX();
        double dirY = posMVy - robot.getY();
        double angleToTarget = Math.toDegrees(Math.atan2(dirX, dirY));

        //Calcul de la diferencia d'angle respecte a la orientacio del robot
        double angle = normalizeAngle(angleToTarget - robot.getHeading());
        robot.setTurnRight(angle);

        //Calcul de quant ha de girar el cos del robot i ajustar el radar
        double radarA = normalizeAngle(robot.getHeading() - robot.getRadarHeading());
        robot.setTurnRadarRight(radarA);

        //Calcul de quant ha de recorrer el robot fins a la cantonada
        double distance = Math.hypot(dirX, dirY);
        robot.setAhead(distance);

        // Tenim el radar oscilant per poder detectar els enemics de cami a la cantonada
        oscillatingRadar();

        // Detectem si el robot ha arribat a la cantonada
        if (robot.getX() == posMVx && robot.getY() == posMVy) {
            corner = true;
        }

        robot.execute();

        // Canviem d'estat quan el robot ha arribat a la cantonada
        if (corner) {
            Estat_SH estatSH = new Estat_SH(robot);
            robot.setEstat(estatSH);
        }

    }

    /**
     * Mètode que es crida quan el robot detecta un altre robot. Si l'enemic
     * està a una distància propera, el robot dispara.
     *
     * @param event L'esdeveniment de detecció d'un robot.
     */
    @Override
    void onScannedRobot(ScannedRobotEvent event) {

        double distanceFromEnemy = event.getDistance();

        if (distanceFromEnemy < 150) {
            robot.fire(3);
            avoidEnemies();
        }

        robot.execute();

    }

    /**
     * Mètode que es estigui buscant enemics quan s'esta dirigint a la
     * cantonada.
     */
    private void oscillatingRadar() {
        if (radarOscillation) {
            robot.setTurnRadarLeft(22.5);
            robot.setTurnRadarRight(22.5);
        }

        radarOscillation = !radarOscillation;
    }

    /**
     * Mètode per evitar als enemics detectats.
     */
    private void avoidEnemies() {
        robot.setTurnRight(45);
        robot.setTurnRadarRight(90);
        robot.setAhead(20);
        robot.turnLeft(45);
        robot.setTurnRadarRight(90);
        robot.setAhead(100);
    }

    /**
     * Mètode que es crida quan es xoquem amb un obstacle.
     *
     * @param event L'esdeveniment de xoc amb un robot.
     */
    @Override
    void onHitRobot(HitRobotEvent event) {
        robot.setBack(100);
        robot.turnRadarRight(360);
    }

}
