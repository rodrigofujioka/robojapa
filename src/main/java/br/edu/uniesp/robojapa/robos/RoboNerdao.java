package br.edu.uniesp.robojapa.robos;

import robocode.*;
import robocode.util.Utils;

/**
 * RoboNerdao - Um robô avançado que usa matemática e estatística para combate
 * 
 * Características:
 * - Movimento em padrões circulares e elípticos
 * - Previsão de balas usando álgebra linear
 * - Adaptação ao estilo de jogo do oponente
 */
public class RoboNerdao extends AdvancedRobot {
    // Constantes para controle de movimento
    private static final int MOVEMENT_STRAIGHT = 0;
    private static final int MOVEMENT_CIRCLE = 1;
    private static final int MOVEMENT_SQUARE = 2;
    
    private int movementPattern = MOVEMENT_CIRCLE;
    private int movementStep = 0;
    private double lastEnemyEnergy = 100;
    private double battlefieldWidth;
    private double battlefieldHeight;
    private double lastEnemyX = 0;
    private double lastEnemyY = 0;
    
    public void run() {
        // Configuração visual
        setBodyColor(java.awt.Color.BLUE);
        setGunColor(java.awt.Color.WHITE);
        setRadarColor(java.awt.Color.CYAN);
        
        // Configuração de batalha
        battlefieldWidth = getBattleFieldWidth();
        battlefieldHeight = getBattleFieldHeight();
        
        // Configuração inicial
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        
        // Loop principal
        while (true) {
            // Escaneia continuamente
            setTurnRadarRight(360);
            
            // Executa padrão de movimento atual
            executeMovementPattern();
            
            // Atualiza o contador de passos
            movementStep++;

            // Muda o padrão de movimento periodicamente
            if (movementStep % 100 == 0) {
                movementPattern = (movementPattern + 1) % 3;
            }
            
            execute();
        }
    }
    
    private void executeMovementPattern() {
        switch (movementPattern) {
            case MOVEMENT_STRAIGHT:
                setAhead(100);
                setTurnRight(10);
                break;
                
            case MOVEMENT_CIRCLE:
                setTurnRight(5);
                setAhead(50);
                break;
                
            case MOVEMENT_SQUARE:
                if (movementStep % 50 < 25) {
                    setAhead(100);
                } else {
                    setBack(100);
                }
                setTurnRight(90);
                break;
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        // Calcula a posição do inimigo
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
        double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
        
        // Atualiza a última posição conhecida do inimigo
        lastEnemyX = enemyX;
        lastEnemyY = enemyY;
        
        // Calcula a mudança de energia para detectar tiros
        double energyChange = lastEnemyEnergy - e.getEnergy();
        
        // Se o inimigo atirou, tenta esquivar
        if (energyChange > 0 && energyChange <= 3) {
            // Esquiva perpendicular ao ângulo do inimigo
            setTurnRight(e.getBearing() + 90);
            setAhead(100);
        }
        
        // Atualiza a energia do inimigo
        lastEnemyEnergy = e.getEnergy();
        
        // Mira e atira
        double bulletPower = Math.min(3, getEnergy() * 0.2);
        double bulletSpeed = 20 - 3 * bulletPower;
        double enemyHeading = e.getHeadingRadians();
        double enemyVelocity = e.getVelocity();
        
        // Previsão de posição do inimigo
        double deltaTime = 0;
        double predictedX = enemyX;
        double predictedY = enemyY;
        
        // Simula a posição futura do inimigo
        while ((++deltaTime) * bulletSpeed < distance(getX(), getY(), predictedX, predictedY)) {
            predictedX += Math.sin(enemyHeading) * enemyVelocity;
            predictedY += Math.cos(enemyHeading) * enemyVelocity;
            
            // Verifica limites do campo de batalha
            if (predictedX < 18.0 || predictedY < 18.0 ||
                predictedX > battlefieldWidth - 18.0 ||
                predictedY > battlefieldHeight - 18.0) {
                predictedX = Math.min(Math.max(18.0, predictedX), battlefieldWidth - 18.0);
                predictedY = Math.min(Math.max(18.0, predictedY), battlefieldHeight - 18.0);
                break;
            }
        }
        
        // Calcula o ângulo para o alvo previsto
        double theta = Utils.normalAbsoluteAngle(Math.atan2(
            predictedX - getX(), predictedY - getY()));
        
        // Vira o canhão para o ângulo calculado
        setTurnGunRightRadians(Utils.normalRelativeAngle(
            theta - getGunHeadingRadians()));
        
        // Atira se o canhão estiver alinhado
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
            fire(bulletPower);
        }
        
        // Mantém o radar travado no inimigo
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
    }
    
    public void onHitByBullet(HitByBulletEvent e) {
        // Muda de direção quando atingido
        setTurnRight(e.getBearing() + 90);
        setAhead(100);
    }
    
    public void onHitWall(HitWallEvent e) {
        // Inverte a direção ao bater na parede
        setBack(100);
        setTurnRight(180);
    }
    
    /**
     * Calcula a distância euclidiana entre dois pontos (x1,y1) e (x2,y2)
     * @param x1 coordenada x do primeiro ponto
     * @param y1 coordenada y do primeiro ponto
     * @param x2 coordenada x do segundo ponto
     * @param y2 coordenada y do segundo ponto
     * @return a distância entre os pontos
     */
    private double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
