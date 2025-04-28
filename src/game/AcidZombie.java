package game;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class AcidZombie extends Zombie {
    private long lastSpitTime = 0;
    private long spitCooldown = 2000; // 2 saniye
    private float spitRange = 200f;
    private BufferedImage acidImage;

    public AcidZombie(float x, float y, Camera cam, Player player) {
        super(x, y, ID.Zombie, cam, player);
        this.maxHealth = 50;
        this.health = maxHealth;
        this.damage = 30;

        try {
            image = ImageIO.read(new File("src/game/zomb1.1.png"));
            acidImage = ImageIO.read(new File("src/game/acid.png"));
        } catch (IOException e) {
            System.out.println("Asit zombi veya asit resmi yüklenemedi!");
        }
    }

    @Override
    public void tick() {
    	   
        float dx = player.getX() + 16 - x;
        float dy = player.getY() + 16 - y;
        followPlayer(0.8f); // yavaş hareket

        // Asit tükür
        long now = System.currentTimeMillis();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= spitRange && now - lastSpitTime > spitCooldown) {
            float angle = (float) Math.atan2(dy, dx);
            shootAcid(angle);
            lastSpitTime = now;
        }

        
        if (this.health <= 0) {
        	System.out.println("Zombi oldu");
            explodeNearby();
            cam.getHandler().removeObject(this); // zombiyi kaldır
            Game.killedZombies++;
        }

    }

    private void shootAcid(float angle) {
        Bullet acid = new Bullet(x, y, ID.Bullet, cam.getHandler()) {
            @Override
            public void tick() {
                x += velX;
                y += velY;

                if (getBounds().intersects(player.getBounds())) {
                    player.takeDamage(15); 
                    cam.getHandler().removeObject(this);
                }
            }

            @Override
            public void render(Graphics g) {
                if (acidImage != null) {
                    g.drawImage(acidImage, (int)x, (int)y, 10, 10, null);
                }
            }

            @Override
            public Rectangle getBounds() {
                return new Rectangle((int)x, (int)y, 10, 10);
            }
        };

        acid.velX = (float) (6 * Math.cos(angle));
        acid.velY = (float) (6 * Math.sin(angle));
        cam.getHandler().addObject(acid);
    }

    private void explodeNearby() {
    	System.out.println("[ASIT ZOMBI] Patladı! Yakındakilere hasar veriliyor...");

        Handler h = cam.getHandler();

       
        System.out.println("[ASIT ZOMBI] Patladı! Yakındaki objelere hasar veriliyor...");
        
        //Yakındaki Zombi Hasarı
        for (GameObject obj : new java.util.LinkedList<>(h.object)) {
            double dist = Math.hypot(obj.getX() - x, obj.getY() - y);

            if (dist < 50) {
                if (obj instanceof Zombie z && obj != this) {
                    z.health -= 80;
                    if (z.health <= 0) {
                        h.removeObject(z);
                        Game.killedZombies++;
                    }
                } else if (obj instanceof Box box) {
                    box.health -= 20;
                }
            }
        }
    }

}
