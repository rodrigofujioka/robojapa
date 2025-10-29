package br.edu.uniesp.robojapa.robos;

import robocode.*;

/**
 * RoboNinja - Um robô furtivo que evita tiros e ataca por trás
 * 
 * Características:
 * - Movimento circular ao redor do inimigo
 * Fuga quando é atingido
 * Dispara apenas quando tem boa mira
 * 
 * Sugestões de evolução:
 * 1. Implementar um radar mais eficiente para rastrear múltiplos inimigos
 * 2. Adicionar padrões de movimento mais imprevisíveis
 * 3. Melhorar a estratégia de fuga quando a energia está baixa
 * 4. Implementar um sistema de aprendizado para prever movimentos do inimigo
 * 5. Adicionar comunicação com outros robôs aliados
 */
public class RoboNinja extends AdvancedRobot {
    boolean movingForward;
    int turnDirection = 1; // 1 para direita, -1 para esquerda
    
    public void run() {
        // Configuração inicial
        setBodyColor(java.awt.Color.BLACK);
        setGunColor(java.awt.Color.DARK_GRAY);
        setRadarColor(java.awt.Color.RED);
        
        // Movimento circular inicial
        setAhead(40000);
        movingForward = true;
        
        // Loop principal
        while (true) {
            // Gira o radar continuamente
            setTurnRadarRight(360);
            // Movimento circular suave
            setTurnRight(5 * turnDirection);
            execute();
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        // Calcula a distância até o inimigo
        double distancia = e.getDistance();
        
        // Se o inimigo estiver perto, mantém distância
        if (distancia < 100) {
            setBack(100);
            turnDirection = -turnDirection;
        } 
        // Se estiver longe, se aproxima
        else if (distancia > 200) {
            setAhead(100);
        }
        
        // Mira no inimigo
        double angulo = getHeading() + e.getBearing();
        double anguloArma = normalizeBearing(angulo - getGunHeading());
        setTurnGunRight(anguloArma);
        
        // Dispara se o alvo estiver bem alinhado
        if (Math.abs(anguloArma) < 10) {
            // Ajusta a potência do tiro baseado na distância
            double poder = Math.min(400 / e.getDistance(), 3);
            fire(poder);
        }
    }
    
    public void onHitByBullet(HitByBulletEvent e) {
        // Quando atingido, muda de direção rapidamente
        turnDirection = -turnDirection;
        setAhead(100 * turnDirection);
    }
    
    public void onHitWall(HitWallEvent e) {
        // Inverte a direção ao bater na parede
        turnDirection = -turnDirection;
        setAhead(100 * turnDirection);
    }
    
    // Normaliza o ângulo para ficar entre -180 e 180 graus
    double normalizeBearing(double angulo) {
        while (angulo >  180) angulo -= 360;
        while (angulo < -180) angulo += 360;
        return angulo;
    }
}
