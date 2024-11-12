package follow;

import java.awt.Graphics2D;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;

/**
 * La classe Estat_FL hereta de la classe abstracta Estat i representa l'estat 
 * d'un robot dins el ByteForce, responsable de la navegació en una ruta rectangular, 
 * la gestió de l'oscil·lació del radar, i el maneig de l'escaneig i evasió d'enemics.
 * 
 * El robot segueix una ruta rectangular predefinida, canviant de direcció cada 
 * 15 segons, i reacciona als enemics detectats disparant i evitant-los.
 * 
 * @author Daniel Aagaard i Marc Plana
 */
public class Estat_FL extends Estat {

    private ByteForce robot;
    private int index;
    private boolean radarOscillation = true;
    private long tempsCanvi = System.currentTimeMillis();
    private boolean sentitHorari = true;
    private double[][] cantonades;
    private double posMVx;
    private double posMVy;
    
    /**
     * Constructor de la classe Estat_FL. 
     * 
     * @param robot Referència al robot ByteForce
     * @param index Índex inicial de la cantonada
     */
    public Estat_FL(ByteForce robot, int index) {
        this.robot = robot;
        this.index = index;
    }
    
    /**
     * Mètode principal d'execució. Calcula la ruta rectangular i dirigeix el robot 
     * per les cantonades del camp de batalla.
     */
    @Override
    void run() {
        double width = robot.getBattleFieldWidth();
        double height = robot.getBattleFieldHeight();

        cantonades = new double[][]{
            {100, 80}, // Esquina inferior izquierda
            {100, height - (height * 0.1)}, // Esquina superior izquierda
            {width - (width * 0.1), height - (height * 0.1)}, // Esquina superior derecha
            {width - (width * 0.1), 80}
        };

        rutaRectangular();

        robot.execute();
    }

    /**
     * Mètode que es crida quan s'escaneja un robot enemic. Si l'enemic és a menys 
     * de 150 unitats de distància, el robot dispara i aplica l'evasió.
     * 
     * @param event Esdeveniment d'escaneig del robot enemic
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
     * Mètode que gestiona la recepció de missatges.
     * 
     * @param event Esdeveniment de recepció de missatge
     */
    @Override
    void onMessageReceived(MessageEvent event) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    /**
     * Mètode que pinta elements gràfics en el camp de batalla.
     * 
     * @param g Objecte Graphics2D per a la representació gràfica
     */
    @Override
    void onPaint(Graphics2D g) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * Mètode que fa seguir el robot per una ruta rectangular entre les cantonades 
     * predefinides del camp de batalla. Alterna entre sentit horari i antihorari 
     * cada 15 segons.
     */
    private void rutaRectangular() {
        long tempsActual = System.currentTimeMillis();
        if (tempsActual - tempsCanvi >= 15000) {
            sentitHorari = !sentitHorari;
            tempsCanvi = tempsActual;
        }

        if (estaEnCantonada()) {
            if (sentitHorari) {
                index = (index + 1) % 4;
            } else {
                index = (index - 1 + 4) % 4;
            }

            posMVx = cantonades[index][0];
            posMVy = cantonades[index][1];

            double dirX = posMVx - robot.getX();
            double dirY = posMVy - robot.getY();
            double angleToTarget = Math.toDegrees(Math.atan2(dirX, dirY));

            //Calcul de la diferencia d'angle respecte a la orientacio del robot
            double angle = normalizeAngle(angleToTarget - robot.getHeading());
            robot.turnRight(angle);

            //Calcul de quant ha de girar el cos del robot i ajustar el radar
            double radarA = normalizeAngle(robot.getHeading() - robot.getRadarHeading());
            robot.turnRadarRight(radarA);

            //Calcul de quant ha de recorrer el robot fins a la cantonada
            double distancia = Math.hypot(dirX, dirY);
            robot.setAhead(distancia);

            oscillatingRadar();
        }
    }
    
    /**
     * Verifica si el robot ha arribat a la cantonada actual.
     * 
     * @return true si el robot està a la cantonada actual, false en cas contrari
     */
    private boolean estaEnCantonada() {
        boolean corner = false;
        if (robot.getX() == cantonades[index][0] && robot.getY() == cantonades[index][1]) {
            corner = true;
        }
        
        return corner;
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
    private void avoidEnemies() {
        robot.setTurnRight(45);
        robot.setTurnRadarRight(90);
        robot.setAhead(20);
        robot.turnLeft(45);
        robot.setTurnRadarRight(90);
        robot.setAhead(100);
    }

}
