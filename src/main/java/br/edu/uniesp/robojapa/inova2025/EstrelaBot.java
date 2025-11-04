package br.edu.uniesp.robojapa.inova2025;

import robocode.*;
import java.awt.Color;

public class EstrelaBot extends AdvancedRobot {

    private double previousEnergy = 100;
    private int moveDirection = 1;

    @Override
    public void run() {
        setBodyColor(Color.RED);
        setGunColor(Color.RED);
        setRadarColor(Color.RED);
        setBulletColor(Color.RED);

        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            setTurnRadarRight(360);
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double energyChange = previousEnergy - e.getEnergy();
        previousEnergy = e.getEnergy();

        if (energyChange > 0 && energyChange <= 3) {
            moveDirection = -moveDirection;
            setAhead((e.getDistance() / 4 + 25) * moveDirection);
        }

        double absoluteBearing = getHeading() + e.getBearing();
        double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
        setTurnGunRight(bearingFromGun);

        if (getGunHeat() == 0 && Math.abs(bearingFromGun) < 10) {
            double firePower = Math.min(400 / e.getDistance(), getEnergy() / 10);
            if (firePower > 0.3) fire(firePower);
        }

        setTurnRight(e.getBearing() + 90 - 30 * moveDirection);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        setTurnRight(90 - e.getBearing());
        setAhead(100 * moveDirection);
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        moveDirection = -moveDirection;
        setAhead(100 * moveDirection);
    }

    private double normalRelativeAngleDegrees(double angle) {
        double adjusted = angle % 360;
        if (adjusted > 180) adjusted -= 360;
        if (adjusted < -180) adjusted += 360;
        return adjusted;
    }
}
