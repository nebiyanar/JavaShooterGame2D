package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RocketLauncher extends Gun {
	

	private static BufferedImage rocketImage;

	//static yaptık her seferde olusturmasin diye
    static {
        try {
            rocketImage = ImageIO.read(new File("src/game/rocketlauncherbullet.png"));
        } catch (IOException e) {
            System.out.println("Rocket bullet görseli yüklenemedi!");
        }
    }
	
    public RocketLauncher() {
        super(1, 10, 0); // 1 roket, 10 hızında, 3 yedek
        this.reloadPerBullet=1000;
        
    }

    @Override
    public void fire(float x, float y, float angle, Handler handler) {
        if (!canFire()) return;

        Bullet rocket = new Bullet(x, y, ID.Bullet,handler) {
            @Override
            public void tick() {
                x += velX;
                y += velY;

                //Collision Baktık
                for (GameObject obj : handler.object) {
                    if (obj.getId() == ID.Zombie) {
                        if (getBounds().intersects(obj.getBounds())) {
                            explode();
                            handler.removeObject(this);
                            break;
                        }
                    }
                }
            }

            private void explode() {
                for (GameObject obj : new java.util.LinkedList<>(handler.object)) {
                    if (obj instanceof Zombie zomb) {
                        double dist = Math.hypot(zomb.getX() - x, zomb.getY() - y);
                        if (dist < 70) {
                            zomb.health -= 150;
                            if (zomb.health <= 0) {
                                handler.removeObject(zomb);
                                Game.killedZombies++;
                            }
                        }
                    }
                }
            }


            @Override
            public void render(Graphics g) {
            	

            	if (rocketImage != null) {
                    g.drawImage(rocketImage, (int) x, (int) y, 12, 12, null);
                } else {
                    g.setColor(java.awt.Color.orange);
                    g.fillRect((int) x, (int) y, 12, 12);
                }

            }

            @Override
            public Rectangle getBounds() {
                return new Rectangle((int)x, (int)y, 12, 12);
            }
        };

        rocket.velX = (float) (7 * Math.cos(angle));
        rocket.velY = (float) (7 * Math.sin(angle));
        handler.addObject(rocket);

        currentAmmo--;
        lastFiredTime = System.currentTimeMillis();
    }
}
