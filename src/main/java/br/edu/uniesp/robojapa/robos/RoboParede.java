package br.edu.uniesp.robojapa.robos;

import robocode.*;
import robocode.util.Utils;

/**
 * RoboParede - Um robô que segue as paredes da arena e atira nos inimigos
 * 
 * Comportamento:
 * 1. Começa se movendo em linha reta até encontrar uma parede
 * 2. Ao bater em uma parede, vira 90 graus e começa a segui-la
 * 3. Continua seguindo a parede enquanto procura e atira em inimigos
 */
public class RoboParede extends AdvancedRobot {
    
    // Direção atual do movimento ao longo da parede
    // 0 = cima, 1 = direita, 2 = baixo, 3 = esquerda
    private int direcaoParede = -1;
    
    // Distância para manter da parede
    private static final int DISTANCIA_PAREDE = 50;
    
    // Contador para controle do radar
    private int contadorRadar = 0;
    
    /**
     * Comportamento principal do robô
     */
    public void run() {
        // Configuração inicial
        setAdjustRadarForGunTurn(true);  // Radar independente do movimento do robô
        setAdjustGunForRobotTurn(true);  // Arma independente do movimento do robô
        
        // Inicia movendo-se para frente
        setAhead(1000);
        
        // Loop principal
        while (true) {
            // Se não estiver seguindo uma parede, procura uma
            if (direcaoParede == -1) {
                // Gira o radar para procurar inimigos
                setTurnRadarRight(360);
            } else {
                // Ajusta o movimento para seguir a parede
                ajustarMovimentoParede();
                
                // Mantém o radar girando para procurar inimigos
                contadorRadar++;
                if (contadorRadar > 10) { // A cada 10 ciclos, vira o radar
                    setTurnRadarRight(360);
                    contadorRadar = 0;
                }
            }
            
            // Executa os comandos
            execute();
        }
    }
    
    /**
     * Ajusta o movimento para seguir a parede
     */
    private void ajustarMovimentoParede() {
        double x = getX();
        double y = getY();
        double largura = getBattleFieldWidth();
        double altura = getBattleFieldHeight();
        
        // Verifica se está muito longe da parede e ajusta
        switch (direcaoParede) {
            case 0: // Seguindo a parede de cima
                if (y < altura - DISTANCIA_PAREDE - 10) {
                    // Vira para cima
                    setTurnRight(normalizeBearing(0 - getHeading()));
                } else {
                    // Segue para a direita
                    setTurnRight(normalizeBearing(90 - getHeading()));
                }
                break;
                
            case 1: // Seguindo a parede da direita
                if (x < largura - DISTANCIA_PAREDE - 10) {
                    // Vira para a direita
                    setTurnRight(normalizeBearing(90 - getHeading()));
                } else {
                    // Segue para baixo
                    setTurnRight(normalizeBearing(180 - getHeading()));
                }
                break;
                
            case 2: // Seguindo a parede de baixo
                if (y > DISTANCIA_PAREDE + 10) {
                    // Vira para baixo
                    setTurnRight(normalizeBearing(180 - getHeading()));
                } else {
                    // Segue para a esquerda
                    setTurnRight(normalizeBearing(270 - getHeading()));
                }
                break;
                
            case 3: // Seguindo a parede da esquerda
                if (x > DISTANCIA_PAREDE + 10) {
                    // Vira para a esquerda
                    setTurnRight(normalizeBearing(270 - getHeading()));
                } else {
                    // Segue para cima
                    setTurnRight(normalizeBearing(0 - getHeading()));
                }
                break;
        }
        
        // Mantém o movimento constante
        setAhead(100);
    }
    
    /**
     * Normaliza um ângulo para o intervalo de -180 a 180 graus
     */
    private double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
    
    /**
     * Chamado quando o robô bate em uma parede
     */
    public void onHitWall(HitWallEvent e) {
        // Calcula em qual parede o robô está
        double x = getX();
        double y = getY();
        double largura = getBattleFieldWidth();
        double altura = getBattleFieldHeight();
        
        // Determina em qual parede o robô está
        if (y > altura - 20) { // Parede de cima
            direcaoParede = 0;
            setTurnRight(90);
        } else if (x > largura - 20) { // Parede da direita
            direcaoParede = 1;
            setTurnRight(90);
        } else if (y < 20) { // Parede de baixo
            direcaoParede = 2;
            setTurnRight(90);
        } else if (x < 20) { // Parede da esquerda
            direcaoParede = 3;
            setTurnRight(90);
        }
        
        // Move-se para longe da parede
        setBack(50);
    }
    
    /**
     * Chamado quando um robô é detectado pelo radar
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        // Calcula a posição exata do inimigo
        double anguloAbsoluto = getHeadingRadians() + e.getBearingRadians();
        
        // Aponta a arma para o alvo
        double anguloArma = Utils.normalRelativeAngle(anguloAbsoluto - getGunHeadingRadians());
        setTurnGunRightRadians(anguloArma);
        
        // Atira com potência baseada na distância (quanto mais perto, mais forte)
        fire(Math.min(3, 400 / e.getDistance()));
        
        // Mantém o radar no alvo
        double anguloRadar = Utils.normalRelativeAngle(anguloAbsoluto - getRadarHeadingRadians());
        setTurnRadarRightRadians(anguloRadar * 2);
        
        // Reinicia o contador do radar
        contadorRadar = 0;
    }
}
