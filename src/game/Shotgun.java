package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Shotgun extends Gun {

    public Shotgun() {
        super(5, 60, 0); // Şarjör: 5, ateş hızı: 60, yedek: 25 mermi
        this.reloadPerBullet=400;
        
    }

    @Override
    public void fire(float x, float y, float angle, Handler handler) {
        if (!canFire()) return;

        int pelletCount = 9;
        float spread = (float) Math.toRadians(45); // 9x5 45 derece dagil
        float startAngle = angle - spread / 2;

        for (int i = 0; i < pelletCount; i++) {
            float pelletAngle = startAngle + i * (spread / (pelletCount - 1));

            Bullet pellet = new Bullet(x, y, ID.Bullet, handler) {
                private final float startX = x;
                private final float startY = y;

                @Override
                public void tick() {
                    x += velX;
                    y += velY;

                    for (GameObject obj : new java.util.LinkedList<>(handler.object)) {
                        if (obj instanceof Zombie zombie) {
                            if (getBounds().intersects(zombie.getBounds())) {
                                float dx = x - startX;
                                float dy = y - startY;
                                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                                //Mesafeye göre logaritmik hasar algoritmasi
                                float baseDamage = 50f;
                                float minDamage = 5f;
                                float logFactor = (float) Math.log(distance + 2);
                                float actualDamage = Math.max(minDamage, baseDamage / (0.5f + logFactor));

                                zombie.health -= actualDamage;
                                if (zombie.health <= 0) {
                                    handler.removeObject(zombie);
                                    Game.killedZombies++;
                                }

                                handler.removeObject(this); 
                                break;
                            }
                        }
                    }
                }

                @Override
                public void render(Graphics g) {
                    g.setColor(new Color(139, 69, 19)); 
                    g.fillRect((int) x, (int) y, 4, 4);
                }

                @Override
                public Rectangle getBounds() {
                    return new Rectangle((int) x, (int) y, 4, 4);
                }
            };

            pellet.velX = (float) (10 * Math.cos(pelletAngle));
            pellet.velY = (float) (10 * Math.sin(pelletAngle));
            handler.addObject(pellet);
        }

        currentAmmo--;
        lastFiredTime = System.currentTimeMillis();
    }
}
