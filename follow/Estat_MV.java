package follow;

import java.awt.Graphics2D;
import robocode.MessageEvent;
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
    private int index = 0;
    private boolean radarOscillation = true;
    private boolean corner = false;
    
    /**
     * Constructor de la classe Estat_MV.
     *
     * @param robot El robot controlat.
     */
    public Estat_MV(ByteForce robot) {
        this.robot = robot;
    }
    
    /**
     * Mètode que executa l'estat actual del robot. Es desplaça cap a la posició
     * establerta i ajusta el radar. També comprova si el robot ha arribat a la
     * cantonada.
     */
    @Override
    void run() {
        double width = robot.getBattleFieldWidth();
        double height = robot.getBattleFieldHeight();

        double posX = robot.getX();
        double posY = robot.getY();

        double[] dist1 = {distance(posX, posY, 0, 0), 0, 0};
        double[] dist2 = {distance(posX, posY, 0, height), 0, height};
        double[] dist3 = {distance(posX, posY, width, 0), width, 0};
        double[] dist4 = {distance(posX, posY, width, height), width, height};
        
        double posMVx = 100;
        double posMVy = 80;
        
        // Calcul de la cantonada mes allunyada
        double minDistance = Math.min(Math.min(dist3[0], dist4[0]), Math.min(dist1[0], dist2[0]));
        if (minDistance == dist2[0]) {
            posMVy = height - (height * 0.1);
            index = 1;
        } else if (minDistance == dist3[0]) {
            posMVx = width - (width * 0.1);
            index = 3;
        } else if (minDistance == dist4[0]) {
            posMVx = width - (width * 0.1);
            posMVy = height - (height * 0.1);
            index = 2;
        }
        
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
        
        if(corner){
            Estat_FL estatFL = new Estat_FL(robot, index);
            robot.setEstat(estatFL);
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
    }
    
    /**
     * Mètode que es crida quan rebem un missatge de un membre de l'equip.
     *
     * @param event L'esdeveniment de missatge.
     */
    @Override
    void onMessageReceived(MessageEvent event) {

    }
    
    /**
     * Mètode que es crida per pintar el robot.
     *
     * @param g Grafics del robot.
     */
    @Override
    void onPaint(Graphics2D g) {

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
     * Mètode que es estigui buscant enemics quan s'esta dirigint a la
     * cantonada.
     */
    private void oscillatingRadar() {
        if (radarOscillation) {
            robot.setTurnRadarLeft(10);
            robot.setTurnRadarRight(10);
        }

        radarOscillation = !radarOscillation;
    }
    
    /**
     * Mètode per evitar als enemics detectats.
     */
    private void avoidEnemies()
    {
        robot.setTurnRight(45);
        robot.setTurnRadarRight(90);
        robot.setAhead(20);
        robot.turnLeft(45); 
        robot.setTurnRadarRight(90);
        robot.setAhead(100);
    }

}
