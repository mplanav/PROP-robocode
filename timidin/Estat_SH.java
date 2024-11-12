package timidin;

import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

/**
 * Estat en què el robot es manté vigilant amb el radar oscil·lant a la recerca
 * de robots enemics, per tal de disparar quan es detecti un enemic.
 *
 * @author Daniel Aagaard i Marc Plana
 */
public class Estat_SH extends Estat {

    private ByteForce robot;

    /**
     * Constructor de la classe Estat_SH.
     *
     * @param robot El robot controlat.
     */
    public Estat_SH(ByteForce robot) {
        this.robot = robot;
    }

    /**
     * Mètode que executa l'estat de vigilància, on el radar gira contínuament.
     */
    @Override
    void run() {
        robot.setTurnRadarRight(90);
        robot.execute();
    }

    /**
     * Mètode que es crida quan es detecta un altre robot. El robot ajusta el
     * radar i l'arma per apuntar i dispara.
     *
     * @param event L'esdeveniment de detecció d'un robot.
     */
    @Override
    void onScannedRobot(ScannedRobotEvent event) {
        // Calcul de l'angle del robot detectat
        double angleToEnemy = robot.getHeading() + event.getBearing();

        double radarTurn = normalizeAngle(angleToEnemy - robot.getRadarHeading());
        double gunTurn = normalizeAngle(angleToEnemy - robot.getGunHeading());

        // Gira el radar cap a l'enemic
        robot.setTurnRadarRight(radarTurn);

        // Gira l'arma cap a l'enemic
        robot.setTurnGunRight(gunTurn);

        // Calculem la potencia de fire depenent de la distancia de l'enemic
        double enemyD = event.getDistance();
        double firePower = calculatePower(enemyD);
        robot.fire(firePower);

        robot.execute();
    }

    /**
     * El robot ajusta la potencia de foc depenent de la distancia a la qual es
     * troba.
     *
     * @param distance La distancia a la qual es troba l'enemic.
     */
    private double calculatePower(double distance) {
        if (distance < 50) {
            return 3;
        } else if (distance >= 50 && distance < 100) {
            return 2.5;
        } else if (distance >= 100 && distance < 200) {
            return 2;
        } else if (distance >= 200 && distance < 300) {
            return 1.5;
        } else {
            return 1;
        }
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
