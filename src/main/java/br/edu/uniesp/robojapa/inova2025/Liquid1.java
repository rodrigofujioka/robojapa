package br.edu.uniesp.robojapa.inova2025;

import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Liquid1 extends Robot {

    int direcao = 1; // 1 = sentido horário, -1 = anti-horário
    double distanciaDesejada = 200; // distância média em torno do inimigo

    public void run() {
        // Inicialmente varre o campo com o radar
        while (true) {
            turnRadarRight(360);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double distancia = e.getDistance();

        // Calcular ângulo até o inimigo
        double anguloAbsoluto = getHeading() + e.getBearing();
        double anguloCanhao = normalRelativeAngleDegrees(anguloAbsoluto - getGunHeading());

        // Mirar o canhão
        turnGunRight(anguloCanhao);

        // Disparar se o canhão estiver quase alinhado
        if (Math.abs(anguloCanhao) < 10) {
            double poder = Math.min(400 / distancia, 3);
            fire(poder);
        }

        // Movimento de "meia-lua" em torno do inimigo
        // Mantém uma distância aproximadamente constante
        double diferencaDistancia = distancia - distanciaDesejada;

        // Ajusta distância (aproxima ou afasta)
        if (Math.abs(diferencaDistancia) > 30) {
            ahead(diferencaDistancia * 0.5);
        }

        // Gira em torno do inimigo
        double anguloMovimento = normalRelativeAngleDegrees(e.getBearing() + 90 - (15 * direcao));
        turnRight(anguloMovimento);
        ahead(100 * direcao);

        // Mantém radar ativo
        scan();
    }

    // Se for atingido, muda o sentido da rotação (meia-lua contrária)
    public void onHitByBullet(HitByBulletEvent e) {
        direcao = -direcao;
        turnRight(90 * direcao);
        ahead(100 * direcao);
    }

    // Se bater na parede, muda o sentido também
    public void onHitWall(HitWallEvent e) {
        direcao = -direcao;
        back(50);
    }
}
