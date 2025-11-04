package br.edu.uniesp.robojapa.inova2025;

import robocode.*;
import java.awt.Color;
import java.util.Random;

public class MegaTrollBot extends AdvancedRobot
{
    private int direction = 1; // 1 para frente, -1 para trás
    private Random random = new Random();

    public void run() {
        // Configurações e Cores
        setColors(Color.MAGENTA, Color.BLACK, Color.RED); 
        setAdjustGunForRobotTurn(true);  // O canhão se move independente do corpo
        setAdjustRadarForGunTurn(true);  // O radar se move independente do canhão
        
        // Loop Principal
        while(true) {
            
            // --- Movimento de Alta Agilidade ---
            // A cada 20 ticks, o robô alterna a direção (frente/trás) para evitar a mira.
            if (getTime() % 20 == 0) {
                direction *= -1; // Inverte a direção
                setAhead(100 * direction); // Define um passo curto na nova direção
            }

            // Gira o corpo levemente em uma direção aleatória para quebrar o padrão.
            setTurnRight(direction * random.nextInt(10)); 

            // --- Radar Lock: Gira o radar continuamente ---
            // Isso garante que, se perder o alvo, ele voltará a escanear imediatamente.
            setTurnRadarRight(360); 
            
            // Executa todos os comandos de movimento e rotação.
            execute();
        }
    }

    // Chamado quando o radar detecta outro robô
    public void onScannedRobot(ScannedRobotEvent e) {
        
        // --- 1. Travamento do Radar e Mira ---
        
        // Mira Rápida (Sempre mirando, mesmo em movimento)
        double absoluteBearing = getHeading() + e.getBearing();
        
        // Trava o Radar (Mira diretamente para o alvo)
        double radarTurn = absoluteBearing - getRadarHeading();
        setTurnRadarRight(normalizeBearing(radarTurn));
        
        // Mira o Canhão (Mira diretamente para o alvo)
        double gunTurn = absoluteBearing - getGunHeading();
        setTurnGunRight(normalizeBearing(gunTurn));
        
        // --- 2. Fogo Agressivo e Inteligente ---
        
        // Atira com força máxima se o alvo estiver perto e com menos se estiver longe.
        double firePower = Math.min(3.0, 500 / e.getDistance()); 

        // Atira somente se o canhão estiver apontando e o canhão não estiver superaquecido.
        // A tolerância de 3 graus permite atirar mesmo em movimento.
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 3) { 
            setFire(firePower);
        }
        
        // Faz um novo scan rápido para refinar a mira (importante!).
        scan();
    }

    // Chamado quando o robô bate na parede
    public void onHitWall(HitWallEvent e) {
        // Inverte a direção imediatamente e vira o corpo para longe da parede.
        direction *= -1;
        setAhead(50 * direction); 
        setTurnRight(e.getBearing() + 90); // Vira 90 graus na direção contrária
    }
    
    // Funções Auxiliares
    
    /**
     * Normaliza um ângulo (de -180 a 180 graus), crucial para comandos de giro.
     */
    double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
