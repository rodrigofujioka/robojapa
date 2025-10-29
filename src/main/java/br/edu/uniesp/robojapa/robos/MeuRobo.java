package br.edu.uniesp.robojapa.robos;

import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class MeuRobo extends Robot {

    public void run(){
        while(true){
            ahead(200);
            turnRight(90);
            ahead(100);
            turnGunRight(150);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e){
        fire(2);
    }

    public void onHitWall(HitWallEvent e)
    {
        back(300);
        turnLeft(90);
    }
}
