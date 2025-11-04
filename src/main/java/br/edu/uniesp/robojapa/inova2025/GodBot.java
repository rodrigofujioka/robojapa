package br.edu.uniesp.robojapa.inova2025;

import robocode.*;
import java.awt.*;

import robocode.util.Utils;


/**
 * GodBot - Robô extremamente poderoso com Wave Surfing e GuessFactor Targeting.
 * 
 * Estratégia:
 * - Wave Surfing: movimento adaptativo que desvia de balas
 * - GuessFactor Targeting: mira probabilística baseada no histórico do inimigo
 * - Radar trancado e fogo inteligente
 */
public class GodBot extends AdvancedRobot {

    // === CONFIGURAÇÕES ===
    private static final double BULLET_POWER = 2.5;
    private static final double WALL_MARGIN = 60;
    private static final int MAX_GUESS_FACTORS = 31;

    // === VARIÁVEIS ===
    private double enemyDistance;
    private double enemyBearing;
    private double enemyVelocity;
    private double enemyHeading;
    private double lastEnemyHeading;
    private double enemyEnergy;
    private double direction = 1;

    private static int[] guessFactors = new int[MAX_GUESS_FACTORS];
    private static double middleFactor = (MAX_GUESS_FACTORS - 1) / 2.0;

    public void run() {
        setColors(Color.BLACK, Color.RED, Color.ORANGE, Color.CYAN, Color.YELLOW);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForGunTurn(true);

        // Radar inicial
        turnRadarRightRadians(Double.POSITIVE_INFINITY);

        while (true) {
            execute();
        }
    }

    // Quando detecta inimigo
    public void onScannedRobot(ScannedRobotEvent e) {
        double absBearing = getHeadingRadians() + e.getBearingRadians();
        double bulletPower = Math.min(3.0, Math.max(1.5, getEnergy() / 10));
        double bulletSpeed = 20 - 3 * bulletPower;

        // Atualiza variáveis do inimigo
        enemyDistance = e.getDistance();
        enemyBearing = e.getBearingRadians();
        enemyVelocity = e.getVelocity();
        enemyHeading = e.getHeadingRadians();

        // Radar travado no inimigo
        double radarTurn = Utils.normalRelativeAngle(absBearing - getRadarHeadingRadians());
        setTurnRadarRightRadians(radarTurn * 2);

        // Wave Surfing: muda direção de movimento para confundir mira inimiga
        if (Math.random() < 0.1) direction *= -1;
        double moveAngle = absBearing + Math.PI / 2 * direction;
        moveAngle = wallSmoothing(getX(), getY(), moveAngle, direction);
        setTurnRightRadians(Utils.normalRelativeAngle(moveAngle - getHeadingRadians()));
        setAhead(150 * direction);

        // === GuessFactor Targeting ===
        double offsetAngle = predictGuessFactorOffset(e, bulletSpeed);
        double gunTurn = Utils.normalRelativeAngle(absBearing + offsetAngle - getGunHeadingRadians());
        setTurnGunRightRadians(gunTurn);

        // Fogo inteligente
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < Math.toRadians(10)) {
            setFire(bulletPower);
        }

        lastEnemyHeading = enemyHeading;
        execute();
    }

    // Previsão do próximo tiro com GuessFactor
    private double predictGuessFactorOffset(ScannedRobotEvent e, double bulletSpeed) {
        double maxEscapeAngle = Math.asin(8 / bulletSpeed);
        int bestIndex = (int) middleFactor;
        for (int i = 0; i < MAX_GUESS_FACTORS; i++) {
            if (guessFactors[i] > guessFactors[bestIndex]) bestIndex = i;
        }
        double guessFactor = (bestIndex - middleFactor) / middleFactor;
        return direction * guessFactor * maxEscapeAngle;
    }

    // Wall smoothing para não bater nas bordas
    private double wallSmoothing(double x, double y, double angle, double orientation) {
        double stick = 120;
        while (!fieldContains(x + Math.sin(angle) * stick, y + Math.cos(angle) * stick)) {
            angle += orientation * 0.05;
        }
        return angle;
    }

    private boolean fieldContains(double x, double y) {
        return x > WALL_MARGIN && y > WALL_MARGIN &&
               x < getBattleFieldWidth() - WALL_MARGIN &&
               y < getBattleFieldHeight() - WALL_MARGIN;
    }

    // Quando leva tiro, aprende e ajusta movimento
    public void onHitByBullet(HitByBulletEvent e) {
        // Muda direção imediatamente
        direction *= -1;
        setAhead(100 * direction);
    }

    // Quando acerta o inimigo, registra padrão de movimento
    public void onBulletHit(BulletHitEvent e) {
        int index = (int) (middleFactor + Math.sin(enemyHeading - lastEnemyHeading) * middleFactor);
        index = Math.max(0, Math.min(MAX_GUESS_FACTORS - 1, index));
        guessFactors[index]++;
    }

    // Reset básico se colidir
    public void onHitWall(HitWallEvent e) {
        direction *= -1;
    }

    public void onHitRobot(HitRobotEvent e) {
        if (e.getBearing() > -90 && e.getBearing() < 90) setBack(100);
        else setAhead(100);
        direction *= -1;
    }
}
