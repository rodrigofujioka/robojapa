package br.edu.uniesp.robojapa.robos;

import robocode.*;
import robocode.util.Utils;

/**
 * RoboSpartano - Um robô de combate corpo a corpo que enfrenta os inimigos de frente
 * 
 * Características:
 * - Ataque frontal agressivo
 * - Perseguição implacável
 * - Táticas de esquiva de tiros
 * 
 * Sugestões de evolução:
 * 1. Implementar um sistema de previsão de balas mais preciso
 * 2. Adicionar padrões de movimento em zigue-zague ao se aproximar
 * 3. Criar um sistema de energia que priorize defesa quando estiver com pouca vida
 * 4. Implementar um radar que priorize alvos mais fracos
 * 5. Adicionar um sistema de fuga quando a energia estiver muito baixa
 */
public class RoboSpartano extends AdvancedRobot {
    int moveDirection = 1; // 1 para frente, -1 para trás
    double previousEnergy = 100; // Energia do inimigo no último scan
    
    public void run() {
        // Configuração visual
        setBodyColor(java.awt.Color.RED);
        setGunColor(java.awt.Color.BLACK);
        setRadarColor(java.awt.Color.YELLOW);
        
        // Configuração inicial
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        
        // Loop principal
        while (true) {
            // Gira o radar para encontrar inimigos
            setTurnRadarRight(360);
            // Movimento de vaivém
            setAhead(100 * moveDirection);
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // Calcula a posição exata do robô inimigo
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        
        // Calcula a velocidade da bala para acertar o alvo em movimento
        double bulletPower = 3; // Poder máximo para causar mais dano
        double enemyVelocity = e.getVelocity() * Math.sin(e.getHeadingRadians() - absoluteBearing);
        
        // Mira no inimigo considerando seu movimento
        setTurnGunRightRadians(Utils.normalRelativeAngle(
            absoluteBearing - getGunHeadingRadians() + 
            (enemyVelocity / (20 - 3 * bulletPower))));
        
        // Se o canhão estiver alinhado, atira
        if (getGunHeat() == 0) {
            fire(bulletPower);
        }
        
        // Tática de esquiva: detecta quando o inimigo atirou
        double changeInEnergy = previousEnergy - e.getEnergy();
        if (changeInEnergy > 0 && changeInEnergy <= 3) {
            // Inverte a direção quando o inimigo atira
            moveDirection = -moveDirection;
            setAhead(150 * moveDirection);
        }
        previousEnergy = e.getEnergy();
        
        // Persegue o inimigo
        setTurnRight(e.getBearing() + 90 - (15 * moveDirection));
    }
    
    public void onHitByBullet(HitByBulletEvent e) {
        // Quando atingido, vira para o inimigo e avança
        turnRight(e.getBearing());
        ahead(100);
    }
    
    public void onHitWall(HitWallEvent e) {
        // Inverte a direção ao bater na parede
        moveDirection = -moveDirection;
        setAhead(100 * moveDirection);
    }
}
