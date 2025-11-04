package br.edu.uniesp.robojapa.inova2025;

import robocode.*;
import java.awt.Color;

public class RoboJesus extends Robot {
    private boolean emMovimentoSenoidal = false;
    private double energiaMaxima;

    public void run() {
        setColors(Color.blue, Color.black, Color.red);
        energiaMaxima = getEnergy();  // energia inicial máxima

        double largura = getBattleFieldWidth();
        double altura = getBattleFieldHeight();

        while (true) {
            double vida = getEnergy();

            if (vida < (energiaMaxima / 2)) {
                emMovimentoSenoidal = true;
                movimentoSenoidal(largura, altura);
                turnRadarRight(45);
            } else {
                emMovimentoSenoidal = false;
                andarPeloPerimetro(largura, altura);
                turnRadarRight(360);
            }
        }
    }

    private void andarPeloPerimetro(double largura, double altura) {
        ahead(largura - 40);
        turnRight(90);
        ahead(altura - 40);
        turnRight(90);
        ahead(largura - 40);
        turnRight(90);
        ahead(altura - 40);
        turnRight(90);
    }

    private void movimentoSenoidal(double largura, double altura) {
        double tempoAtual = System.currentTimeMillis() / 1000.0;
        double anguloSenoidal = 20 * Math.sin(tempoAtual * Math.PI);


        if (getX() < 50 || getX() > largura - 50 || getY() < 50 || getY() > altura - 50) {
            back(50);
            turnRight(90);
            ahead(50);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double angulo = e.getBearing();

        if (getEnergy() < (energiaMaxima / 2)) {
            turnGunRight(angulo);
            fire(20);
            turnGunRight(22.5);
            fire(3);
            turnGunLeft(45);
            fire(3);
            turnGunRight(22.5);
        } else {
            turnGunRight(angulo);
            fire(10);
            turnGunRight(15);
            fire(5);
            turnGunLeft(30);
            fire(5);
            turnGunRight(15);
            fire(3);
        }
    }

    public void onHitWall(HitWallEvent e) {
        if (emMovimentoSenoidal) {
            back(50);
            turnRight(90);
            ahead(50);
        } else {
            back(50);
            turnRight(90);
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        if (emMovimentoSenoidal) {
            back(100);
            if (getX() < 50 || getX() > (getBattleFieldWidth() - 50) || getY() < 50 || getY() > (getBattleFieldHeight() - 50)) {
                turnRight(90);
                ahead(50);
            }
        } else {
            back(100);
        }
    }

    public void onHitRobot(HitRobotEvent e) {
        back(50);
        while (getOthers() > 0 && e.getEnergy() < 50) {
            back(50);
        }
    }
}
