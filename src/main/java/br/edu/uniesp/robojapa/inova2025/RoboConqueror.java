package br.edu.uniesp.robojapa.inova2025;

import robocode.*;
import robocode.util.Utils; // Utils é do pacote robocode.util, que está dentro de robocode.*

public class RoboConqueror extends AdvancedRobot {

        private static final double WALL_STICK_DISTANCE = 110; // Distância preferencial da parede
        private double lastEnemyEnergy = 100; // Para detecção de tiros

        public void run() {
            // Configuração visual
            setBodyColor(java.awt.Color.MAGENTA);
            setGunColor(java.awt.Color.ORANGE);
            setRadarColor(java.awt.Color.WHITE);

            // Configuração de batalha
            setAdjustGunForRobotTurn(true);
            setAdjustRadarForGunTurn(true);

            // Loop principal
            while (true) {
                // Movimento de Evasão Avançado (Caótico)
                doEvasiveMovement();

                // Radar: Escaneia 360 graus até encontrar o alvo (ou se estiver travado)
                setTurnRadarRight(360);

                execute();

            }
        }

        /**
         * Implementa um movimento de Random Walk Caótico com evasão de parede.
         */
        private void doEvasiveMovement() {
            // 1. Inverte a direção se estiver muito perto da parede
            if (getX() < WALL_STICK_DISTANCE || getY() < WALL_STICK_DISTANCE ||
                    getX() > getBattleFieldWidth() - WALL_STICK_DISTANCE ||
                    getY() > getBattleFieldHeight() - WALL_STICK_DISTANCE) {

                // Calcula o ângulo para fugir da parede
                double angleToWall = calculateWallAngle();
                // Vira para fugir da parede
                setTurnRight(Utils.normalRelativeAngleDegrees(angleToWall - getHeading()));
                setAhead(180);
                return;
            }

            // 2. Movimento principal: Escolhe uma pequena mudança aleatória na direção
            if (getDistanceRemaining() == 0) {
                // Escolhe um novo ângulo aleatório (±30 graus)
                double turnAmt = Math.random() * 60 - 30;
                setTurnRight(turnAmt);

                // Escolhe uma distância aleatória (entre 50 e 150)
                setAhead(Math.random() * 100 + 50);
            }
        }


        /**
         * Calcula o ângulo absoluto para virar e evitar a parede mais próxima.
         * @return Ângulo absoluto em graus (0=Norte, 90=Leste)
         */
        private double calculateWallAngle() {
            double x = getX(), y = getY();
            double width = getBattleFieldWidth(), height = getBattleFieldHeight();

            // Determina o ângulo de fuga (perpendicular à parede)
            if (x < WALL_STICK_DISTANCE) return 90; // Parede esquerda: fugir para Leste
            if (x > width - WALL_STICK_DISTANCE) return 270; // Parede direita: fugir para Oeste
            if (y < WALL_STICK_DISTANCE) return 0; // Parede inferior: fugir para Norte
            if (y > height - WALL_STICK_DISTANCE) return 180; // Parede superior: fugir para Sul

            return 0;
        }

        // --- Estratégia de Mira Aprimorada (Previsão Linear) ---

        public void onScannedRobot(ScannedRobotEvent e) {
            // 1. Cálculos de Posição Base
            double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
            double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
            double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);

            // Poder do tiro: Ajusta com base na distância/energia (similar ao Nerdao)
            double bulletPower = Math.min(3.0, getEnergy() * 0.2);
            if (e.getDistance() < 150) {
                bulletPower = 3.0; // Alto poder em curta distância
            } else if (e.getDistance() > 500) {
                bulletPower = 2.0; // Baixo poder em longa distância
            }

            double bulletSpeed = 20 - 3 * bulletPower;

            // 2. Esquiva Reativa Aprimorada (Detecção de Tiro)
            double energyChange = lastEnemyEnergy - e.getEnergy();
            if (energyChange > 0.1 && energyChange <= 3.0) {
                // O inimigo atirou. Esquiva-se (perpendicular ao ângulo dele)
                setTurnRight(e.getBearing() + 120);
                setAhead(120);
            }
            lastEnemyEnergy = e.getEnergy();

            // 3. Previsão da Posição Futura (Linear Targeting)
            double deltaTime = 0;
            double predictedX = enemyX;
            double predictedY = enemyY;
            double enemyHeading = e.getHeadingRadians();
            double enemyVelocity = e.getVelocity();

            // Simula o avanço do inimigo no tempo de voo da bala
            while ((++deltaTime) * bulletSpeed < distance(getX(), getY(), predictedX, predictedY)) {
                predictedX += Math.sin(enemyHeading) * enemyVelocity;
                predictedY += Math.cos(enemyHeading) * enemyVelocity;

                // Verifica limites do campo de batalha (Simulação de rebote do inimigo)
                if (predictedX < 18.0 || predictedY < 18.0 ||
                        predictedX > getBattleFieldWidth() - 18.0 ||
                        predictedY > getBattleFieldHeight() - 18.0) {

                    // Em vez de quebrar, tenta uma previsão simples de parede:
                    predictedX = Math.min(Math.max(18.0, predictedX), getBattleFieldWidth() - 18.0);
                    predictedY = Math.min(Math.max(18.0, predictedY), getBattleFieldHeight() - 18.0);
                    // Aumenta a precisão forçando a posição para dentro, mas a mira é interrompida aqui.
                    break;
                }
            }

            // 4. Mira e Fogo

            // Calcula o ângulo para a posição prevista
            double theta = Utils.normalAbsoluteAngle(Math.atan2(
                    predictedX - getX(), predictedY - getY()));

            // Gira o canhão
            setTurnGunRightRadians(Utils.normalRelativeAngle(
                    theta - getGunHeadingRadians()));

            // Fogo se o canhão estiver alinhado e frio
            if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
                fire(bulletPower);
            }

            // 5. Trava o Radar no Alvo
            // Gira o radar para compensar o movimento do robô e o movimento do canhão,
            // mantendo o alvo no centro.
            double radarTurn = Utils.normalRelativeAngleDegrees(absoluteBearing - getRadarHeadingRadians());
            setTurnRadarRightRadians(radarTurn * 1.5); // Multiplica por 1.5 para travar melhor.
        }

        // --- Funções de Utilitário ---

        /**
         * Calcula a distância euclidiana entre dois pontos.
         */
        private double distance(double x1, double y1, double x2, double y2) {
            double dx = x2 - x1;
            double dy = y2 - y1;
            return Math.sqrt(dx * dx + dy * dy);
        }

        public void onHitByBullet(HitByBulletEvent e) {
            // Implementação básica de esquiva quando atingido
            setTurnRight(e.getBearing() + 90);
            setAhead(200);
        }

        public void onHitWall(HitWallEvent e) {
            // Recua e vira para longe da parede
            setBack(100);
            setTurnRight(180);
        }
}
