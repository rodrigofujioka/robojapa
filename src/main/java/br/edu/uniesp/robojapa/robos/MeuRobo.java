package br.edu.uniesp.robojapa.robos;

import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class MeuRobo extends Robot {

    /**
     * Método principal que controla o comportamento do robô.
     * Este método é executado continuamente enquanto o robô estiver ativo.
     */
    public void run(){
        // Loop infinito que mantém o robô em movimento contínuo
        while(true){
            // Move o robô para frente por 200 unidades de distância
            ahead(200);
            
            // Gira o robô 90 graus para a direita
            turnRight(90);
            
            // Move o robô para frente por mais 100 unidades
            ahead(100);
            
            // Gira a arma do robô 150 graus para a direita
            // (a arma pode girar independentemente do corpo do robô)
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
