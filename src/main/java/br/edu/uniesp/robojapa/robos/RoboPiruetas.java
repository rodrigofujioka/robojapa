package br.edu.uniesp.robojapa.robos;

import robocode.*;
import robocode.util.Utils;

/**
 * RoboPiruetas - Um robô que fica girando, atirando aleatoriamente
 * e persegue outros robôs quando detectados.
 */
public class RoboPiruetas extends AdvancedRobot {

    boolean alvoBloqueado = false;
    double direcaoAlvo = 0;
    double distanciaAlvo = 0;

    /**
     * Comportamento principal do robô
     */
    public void run() {
        // Configuração inicial
        setAdjustRadarForGunTurn(true);  // Radar independente do movimento do robô
        setAdjustGunForRobotTurn(true);  // Arma independente do movimento do robô

        // Loop principal do robô
        while (true) {
            // Se não tiver um alvo, gira o radar para encontrar um
            if (!alvoBloqueado) {
                setTurnRadarRight(360);  // Gira o radar 360 graus
            } else {
                // Se tiver um alvo, aponta o radar para ele
                double girarRadar = getHeadingRadians() + direcaoAlvo - getRadarHeadingRadians();
                setTurnRadarRightRadians(Utils.normalRelativeAngle(girarRadar));

                // Aponta a arma para o alvo
                double girarArma = getHeadingRadians() + direcaoAlvo - getGunHeadingRadians();
                setTurnGunRightRadians(Utils.normalRelativeAngle(girarArma));

                // Move-se em direção ao alvo
                double anguloGiro = Utils.normalRelativeAngleDegrees(
                        direcaoAlvo - getHeadingRadians() + 90);
                setTurnRight(anguloGiro);
                setAhead(distanciaAlvo / 2);

                // Atira com potência baseada na distância (quanto mais perto, mais forte)
                fire(Math.min(400 / distanciaAlvo, 3));
            }

            // Executa todos os comandos pendentes
            execute();
        }
    }

    /**
     * Chamado quando um robô é detectado pelo radar
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        // Atualiza as informações do alvo
        alvoBloqueado = true;
        direcaoAlvo = e.getBearingRadians();
        distanciaAlvo = e.getDistance();

        // Dispara um tiro com potência baseada na distância
        fire(Math.min(400 / e.getDistance(), 3));
    }

    /**
     * Chamado quando o robô bate na parede
     */
    public void onHitWall(HitWallEvent e) {
        // Inverte a direção quando bate na parede
        setBack(100);
        setTurnRight(90);
    }

    /**
     * Chamado quando o robô colide com outro robô
     */
    public void onHitRobot(HitRobotEvent e) {
        // Atira à queima-roupa quando colide com outro robô
        fire(3);  // Tiro na potência máxima
        back(50); // Dá uma afastada
    }

    /**
     * Chamado quando um robô é destruído
     */
    public void onRobotDeath(RobotDeathEvent e) {
        // Se o alvo atual morreu, desbloqueia para procurar outro
        alvoBloqueado = false;
    }

    /**
     * Chamado quando o radar completa uma varredura de 360 graus
     */
    public void onSweepComplete() {
        // Se não encontrou ninguém, continua girando e se movendo
        if (!alvoBloqueado) {
            setTurnRight(45);
            setAhead(100);
            setTurnRadarRight(360);
        }
    }
}
