package br.edu.uniesp.robojapa.inova2025;

import robocode.*;
import java.awt.Color;
import java.awt.geom.Point2D;

public class WizeMal extends AdvancedRobot {

    private boolean movingForward = true;

    public void run() {
        setColors(Color.red, Color.black, Color.orange);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        // Mantém o radar sempre girando
        while (true) {
            setTurnRadarRight(360);
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double enemyBearing = e.getBearing();
        double enemyDistance = e.getDistance();
        double enemyHeading = e.getHeading();
        double enemyVelocity = e.getVelocity();

        // 🔭 Trava o radar no inimigo
        double radarTurn = getHeading() - getRadarHeading() + enemyBearing;
        setTurnRadarRight(normalizeBearing(radarTurn) * 2);

        // 🔫 Potência máxima sempre
        double firePower = 3;
        double bulletSpeed = 20 - 3 * firePower;

        // 📍 Coordenadas atuais
        double myX = getX();
        double myY = getY();
        double absBearing = Math.toRadians(getHeading() + enemyBearing);

        // Posição do inimigo
        double enemyX = myX + Math.sin(absBearing) * enemyDistance;
        double enemyY = myY + Math.cos(absBearing) * enemyDistance;

        // 🔮 Predição de onde o inimigo estará
        double predictedX = enemyX;
        double predictedY = enemyY;
        double deltaTime = 0;

        while ((++deltaTime) * bulletSpeed < Point2D.distance(myX, myY, predictedX, predictedY)) {
            predictedX = enemyX + Math.sin(Math.toRadians(enemyHeading)) * enemyVelocity * deltaTime;
            predictedY = enemyY + Math.cos(Math.toRadians(enemyHeading)) * enemyVelocity * deltaTime;

            // Evita prever fora da arena
            if (predictedX < 18.0 || predictedY < 18.0 ||
                    predictedX > getBattleFieldWidth() - 18.0 ||
                    predictedY > getBattleFieldHeight() - 18.0) {
                break;
            }
        }

        // Mira no ponto previsto
        double theta = Math.toDegrees(Math.atan2(predictedX - myX, predictedY - myY));
        double gunTurn = normalizeBearing(theta - getGunHeading());
        setTurnGunRight(gunTurn);

        // 🔥 Atira se o canhão estiver bem alinhado
        if (Math.abs(gunTurn) < 10 && getGunHeat() == 0) {
            fire(firePower);
        }

        // 🚗 Movimento inteligente: orbita o inimigo, sem bater na parede
        double moveAngle = normalizeBearing(enemyBearing + 90 - 15 * (Math.random() - 0.5));
        setTurnRight(moveAngle);

        // Calcula distância segura das bordas (mantém no meio da arena)
        double margin = 80;
        if (myX < margin || myY < margin ||
                myX > getBattleFieldWidth() - margin ||
                myY > getBattleFieldHeight() - margin) {
            // Se estiver muito perto da parede, vira para o centro
            double angleToCenter = Math.toDegrees(Math.atan2(getBattleFieldWidth() / 2 - myX,
                    getBattleFieldHeight() / 2 - myY));
            setTurnRight(normalizeBearing(angleToCenter - getHeading()));
            setAhead(100);
        } else {
            // Movimento lateral suave
            if (movingForward) {
                setAhead(120);
            } else {
                setBack(120);
            }
        }

        // Chance pequena de inverter movimento para enganar o inimigo
        if (Math.random() < 0.03) {
            movingForward = !movingForward;
        }

        execute();
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // Reação leve: muda a direção, mas sem correr descontrolado
        movingForward = !movingForward;
        setTurnRight(normalizeBearing(90 - (getHeading() - e.getHeading())));
        setAhead(100);
    }

    public void onHitWall(HitWallEvent e) {
        // Ao bater na parede, recua e gira de volta para o centro
        movingForward = !movingForward;
        double angleToCenter = Math.toDegrees(Math.atan2(getBattleFieldWidth() / 2 - getX(),
                getBattleFieldHeight() / 2 - getY()));
        setTurnRight(normalizeBearing(angleToCenter - getHeading()));
        setAhead(100);
    }

    private double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
