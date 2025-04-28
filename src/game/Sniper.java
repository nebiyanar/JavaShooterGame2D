package game;

import java.awt.Rectangle;

public class Sniper extends Gun {

    public Sniper() {
        super(5, 30, 0);
        this.reloadPerBullet=500;
    }

    
    @Override
    public void fire(float x, float y, float angle, Handler handler) {
        if (!canFire()) return;

        Bullet piercingBullet = new Bullet(x, y, ID.Bullet, handler) {
            private float damage = 100f;
            private java.util.Set<Zombie> hitZombies = new java.util.HashSet<>();

            @Override
            public void tick() {
                x += velX;
                y += velY;

                for (GameObject obj : new java.util.LinkedList<>(handler.object)) {
                    if (obj instanceof Zombie zombie && !hitZombies.contains(zombie)) {
                        if (getBounds().intersects(zombie.getBounds())) {
                            zombie.health -= damage;
                            hitZombies.add(zombie); 

                            if (zombie.health <= 0) {
                                handler.removeObject(zombie);
                                Game.killedZombies++;
                            }

                            damage *= 0.5f;

                            if (damage < 10) {
                                handler.removeObject(this);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public Rectangle getBounds() {
                return new Rectangle((int)x, (int)y, 8, 8);
            }
        };

        piercingBullet.velX = (float)(15 * Math.cos(angle));
        piercingBullet.velY = (float)(15 * Math.sin(angle));
        handler.addObject(piercingBullet);

        currentAmmo--;
        lastFiredTime = System.currentTimeMillis();
    }


}
