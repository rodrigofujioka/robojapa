package br.edu.uniesp.robojapa.inova2025;

import robocode.*;
import java.awt.Color;
import java.util.Random;

public class Robo008 extends AdvancedRobot {
    private int moveDirection = 1;
    private double lastEnemyEnergy = 100;
    private int scanDirection = 1;
    private Random random = new Random();
    private int bulletsHit = 0;
    private int rapidFireMode = 0;
    private long lastScanTime = 0;

    public void run() {
        // Apariencia de máquina de destrucción total
        setBodyColor(new Color(0, 0, 0));
        setGunColor(new Color(255, 0, 0));
        setRadarColor(new Color(255, 0, 0));
        setBulletColor(new Color(255, 255, 0));
        setScanColor(new Color(255, 0, 0));

        // Configuración de independencia de componentes
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        // MÁXIMA VELOCIDAD DE DISPARO
        setAdjustGunForRobotTurn(true);

        // Comportamiento de ametralladora infernal
        while (true) {
            // Movimiento agresivo constante
            setMaxVelocity(8);
            setAhead(150 * moveDirection);
            setTurnRight(30);

            // RADAR ULTRA-RÁPIDO - más detecciones = más disparos
            setTurnRadarRight(360 * 5); // 5 veces más rápido!

            // DISPAROS PREVENTIVOS CONSTANTES - LLUVIA DE BALAS
            if (getGunHeat() == 0) {
                // Dispara aleatoriamente mientras busca enemigos
                if (random.nextInt(3) == 0) { // 33% de probabilidad cada ciclo
                    setFire(0.7 + random.nextDouble() * 1.3);
                }
            }

            // MODO RÁFAGA ACTIVO - descarga continua de balas
            if (rapidFireMode > 0) {
                rapidFireMode--;
                if (getGunHeat() == 0) {
                    setFire(1.0 + random.nextDouble() * 2.0);
                }
            }

            // DISPAROS DE ÁREA cada cierto tiempo
            if (getTime() % 30 == 0 && getGunHeat() == 0) {
                setFire(2.5); // Disparo potente de área
            }

            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        lastScanTime = getTime();

        // **MODO DISPARO RÁPIDO ACTIVADO** - ¡MÁXIMA CADENCIA!
        rapidFireMode = 5; // 5 ciclos de disparo rápido

        // CÁLCULO DE DISPARO PRECISO
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
        double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);

        // Predicción de movimiento enemigo
        double enemyHeading = e.getHeadingRadians();
        double enemyVelocity = e.getVelocity();

        // **DISPARO PRINCIPAL DE ALTA POTENCIA**
        double bulletPower = Math.min(3.0, getEnergy() / 3); // Más agresivo
        double bulletSpeed = 20 - 3 * bulletPower;
        double deltaTime = e.getDistance() / bulletSpeed;

        double futureX = enemyX + enemyVelocity * deltaTime * Math.sin(enemyHeading);
        double futureY = enemyY + enemyVelocity * deltaTime * Math.cos(enemyHeading);

        double absDeg = absoluteBearing(futureX, futureY);

        // Girar el cañón rápidamente
        setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(
                absDeg - getGunHeadingRadians()));

        // **SISTEMA DE DISPARO MÚLTIPLE**
        if (getGunHeat() == 0) {
            // DISPARO 1: Principal de alta potencia
            setFire(bulletPower);

            // DISPARO 2: Secundario inmediato (si hay energía)
            if (getEnergy() > 5) {
                // Pequeña pausa para permitir el segundo disparo
                execute();
                if (getGunHeat() == 0) {
                    setFire(1.5);
                }
            }
        }

        // **DISPAROS RÁPIDOS ADICIONALES BASADOS EN DISTANCIA**
        if (e.getDistance() < 300) {
            // A corta distancia: TRIPLE DISPARO
            for (int i = 0; i < 2; i++) {
                if (getGunHeat() == 0 && getEnergy() > 3) {
                    setFire(1.0 + i * 0.5);
                }
            }
        }

        // **DISPAROS DE SATURACIÓN** - patrón de múltiples ángulos
        if (e.getDistance() < 200 && getEnergy() > 10) {
            // Dispara en un pequeño abanico para asegurar impacto
            setTurnGunRight(5);
            if (getGunHeat() == 0) {
                setFire(1.0);
            }
            setTurnGunLeft(10);
            if (getGunHeat() == 0) {
                setFire(1.0);
            }
            setTurnGunRight(5); // Volver a posición original
        }

        // DETECCIÓN DE DISPAROS ENEMIGOS - CONTRAATAQUE MASIVO
        double energyChange = lastEnemyEnergy - e.getEnergy();
        if (energyChange > 0 && energyChange <= 3) {
            // **RESPUESTA CON BARRIDO DE BALAS**
            for (int i = 0; i < 3; i++) {
                if (getGunHeat() == 0 && getEnergy() > 2) {
                    setFire(1.0);
                }
            }
        }
        lastEnemyEnergy = e.getEnergy();

        // ESTRATEGIA DE MOVIMIENTO CON MÁS DISPAROS
        if (e.getDistance() < 100) {
            // ATAQUE CORPORAL - disparos máximos
            moveDirection = -1;
            setBack(80);
            setTurnRight(e.getBearing() + 90);

            // DESCARGAR TODAS LAS BALAS POSIBLES
            while (getGunHeat() == 0 && getEnergy() > 1) {
                setFire(Math.min(3.0, getEnergy() - 0.1));
            }
        } else if (e.getDistance() > 400) {
            // Persecución con disparos de largo alcance
            moveDirection = 1;
            setAhead(e.getDistance() / 2);
            setTurnRight(e.getBearing());
        } else {
            // Movimiento lateral con fuego constante
            moveDirection = (Math.abs(e.getBearing()) > 90) ? -1 : 1;
            setAhead(120 * moveDirection);
            setTurnRight(e.getBearing() + 90);
        }

        // RADAR DE BLOQUEO
        double radarTurn = absoluteBearing - getRadarHeadingRadians();
        setTurnRadarRightRadians(2 * robocode.util.Utils.normalRelativeAngle(radarTurn));
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // **CONTRAATAQUE CON TRIPLE RÁFAGA**
        moveDirection = -moveDirection;
        setAhead(150 * moveDirection);
        setTurnLeft(90);

        // DESCARGAR 3 BALAS RÁPIDAS
        for (int i = 0; i < 3; i++) {
            setTurnGunRight(getHeading() - getGunHeading() + e.getBearing() + (i * 10 - 10));
            if (getGunHeat() == 0) {
                setFire(2.0);
            }
        }
    }

    public void onHitWall(HitWallEvent e) {
        moveDirection = -moveDirection;
        setAhead(150 * moveDirection);
        setTurnRight(90 + random.nextInt(90));

        // Disparo de frustración al chocar
        if (getGunHeat() == 0) {
            setFire(2.0);
        }
    }

    public void onHitRobot(HitRobotEvent e) {
        // **EJECUCIÓN A QUEMARROPA CON MÚLTIPLES BALAS**
        setTurnGunRight(getHeading() - getGunHeading() + e.getBearing());

        // DESCARGAR HASTA 5 BALAS EN SECUENCIA RÁPIDA
        for (int i = 0; i < 5 && getEnergy() > 2; i++) {
            if (getGunHeat() == 0) {
                setFire(2.5);
            }
        }
        setBack(80);
    }

    public void onBulletHit(BulletHitEvent e) {
        bulletsHit++;
        // **SEGUIMIENTO AGRESIVO** - más disparos cuando golpeas
        rapidFireMode = 8; // Extiende el modo ráfaga

        // Disparo extra de celebración
        if (getGunHeat() == 0) {
            setFire(2.0);
        }
    }

    public void onBulletMissed(BulletMissedEvent e) {
        // **COMPENSACIÓN** - disparar más cuando falla
        scanDirection = -scanDirection;
        if (getGunHeat() == 0) {
            setFire(1.5); // Disparo extra
        }
    }

    public void onRobotDeath(RobotDeathEvent e) {
        // **CELEBRACIÓN CON LLUVIA DE BALAS**
        for (int i = 0; i < 15; i++) {
            setTurnRight(24);
            setTurnLeft(24);
            if (getGunHeat() == 0 && getEnergy() > 1) {
                setFire(1.0 + random.nextDouble() * 1.5);
            }
            execute();
        }
    }

    public void onWin(WinEvent e) {
        // **VICTORY DANCE INFINITO CON BALAS**
        while (true) {
            setTurnRight(45);
            setTurnLeft(45);
            if (getGunHeat() == 0) {
                setFire(0.5 + random.nextDouble() * 1.0);
            }
            execute();
        }
    }

    // MÉTODOS AUXILIARES DE PRECISIÓN
    private double absoluteBearing(double targetX, double targetY) {
        double xo = targetX - getX();
        double yo = targetY - getY();
        double hyp = Math.sqrt(xo * xo + yo * yo);
        double arcSin = Math.asin(xo / hyp);
        double bearing = 0;

        if (xo > 0 && yo > 0) {
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) {
            bearing = 2 * Math.PI + arcSin;
        } else {
            bearing = Math.PI - arcSin;
        }

        return bearing;
    }
}
