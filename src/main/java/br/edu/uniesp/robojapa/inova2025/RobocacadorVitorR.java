package br.edu.uniesp.robojapa.inova2025;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.HitWallEvent;

public class RobocacadorVitorR extends AdvancedRobot {

    public void run() {
        // loop principal do robô
        while (true) {
            setTurnRadarRight(360); // radar gira o tempo todo
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double distancia = e.getDistance();

        // Mira no inimigo
        double anguloParaInimigo = getHeading() - getGunHeading() + e.getBearing();
        turnGunRight(anguloParaInimigo);

        // Potência do tiro (quanto mais perto, mais forte)
        double potenciaTiro = Math.min(3, 400 / distancia);
        fire(potenciaTiro);

        // Movimento simples para não ficar parado
        if (distancia < 150) {
            back(50);
        } else {
            ahead(50);
        }
    }

    public void onHitWall(HitWallEvent e) {
        back(100);           // recua
        turnRight(90);       // muda direção
    }
}
