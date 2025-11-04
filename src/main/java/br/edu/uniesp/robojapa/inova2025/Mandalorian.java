package br.edu.uniesp.robojapa.inova2025;

import robocode.HitWallEvent;
import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class Mandalorian extends Robot {

    public void run() {
        while (true) {
            // Radar gira continuamente
            turnRadarRight(360);

            // Movimento imprevisível: zigue-zague com mudanças aleatórias
            ahead(60);
            turnRight(Math.random() * 120 - 60); // vira entre -60° e +60°
        }
    }

    // Mira preditiva e atira apenas quando há alta chance de acerto
    public void onScannedRobot(ScannedRobotEvent e) {
        // Distância e velocidade do inimigo
        double distancia = e.getDistance();
        double velocidadeInimigo = e.getVelocity();
        double direcaoInimigo = e.getHeading();

        // Calcula tempo para o projétil atingir o inimigo
        double tempoTiro = distancia / 15; // velocidade média do tiro = 15 pixels por tick

        // Previsão da posição futura do inimigo
        double futuraDirecao = direcaoInimigo;
        double futuraVelocidade = velocidadeInimigo;
        double futuraDistanciaX = distancia * Math.sin(Math.toRadians(futuraDirecao)) * futuraVelocidade * tempoTiro;
        double futuraDistanciaY = distancia * Math.cos(Math.toRadians(futuraDirecao)) * futuraVelocidade * tempoTiro;

        // Calcula ângulo da arma para a posição prevista
        double anguloParaInimigo = getHeading() - getGunHeading() + e.getBearing()
                + Math.toDegrees(Math.atan2(futuraDistanciaX, futuraDistanciaY));

        // Atira somente se a arma estiver quase alinhada com o inimigo
        if (Math.abs(anguloParaInimigo) < 5) { // margem menor para acerto
            turnGunRight(anguloParaInimigo);
            fire(2.5);
        } else {
            // Alinha a arma lentamente sem disparar
            turnGunRight(anguloParaInimigo);
        }
    }

    // Esquiva lateral e contra-ataque automático quando atingido
    public void onHitByBullet(HitByBulletEvent e) {
        // Esquiva lateral aleatória
        double anguloEsquiva = 90 - Math.random() * 180; // entre -90° e +90°
        turnRight(anguloEsquiva);
        ahead(60);

        // Contra-ataque automático com mira preditiva simples
        double anguloParaInimigo = getHeading() - getGunHeading() + e.getBearing();
        if (Math.abs(anguloParaInimigo) < 5) {
            turnGunRight(anguloParaInimigo);
            fire(2.0);
        } else {
            turnGunRight(anguloParaInimigo);
        }

        // Mudança de direção aleatória para ficar imprevisível
        turnRight(Math.random() * 180);
    }

    // Evita ficar preso na parede
    public void onHitWall(HitWallEvent e) {
        back(60);
        turnRight(90 + Math.random() * 90); // vira aleatoriamente após bater
    }
}
