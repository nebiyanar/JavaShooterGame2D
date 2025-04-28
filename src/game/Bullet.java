package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bullet extends GameObject {

    private Handler handler;
    private static BufferedImage bulletImage;

    static {
        try {
            bulletImage = ImageIO.read(new File("src/game/bullet.png"));
        } catch (IOException e) {
            System.out.println("Bullet görseli yüklenemedi!");
        }
    }


    public Bullet(float x, float y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
    }

    @Override
    public void tick() {
        x += velX;
        y += velY;

        for (int i = 0; i < handler.object.size(); i++) {
            GameObject obj = handler.object.get(i);

            if (obj instanceof Zombie zombie) {
                if (getBounds().intersects(zombie.getBounds())) {
                    zombie.health -= 25; 
                    //System.out.println(">> Mermi zombiye çarptı: " + zombie.getClass().getSimpleName() + " | Yeni can: " + zombie.health);

                    if (zombie.health <= 0) {
                        if (zombie instanceof AcidZombie) {
                           
                           
                        } else {
                            handler.removeObject(zombie);
                            Game.killedZombies++;

                            if (zombie instanceof NormalZombie) Game.score += 10;
                            else if (zombie instanceof TankZombie) Game.score += 30;
                            else if (zombie instanceof CrawlerZombie) Game.score += 15;

                           
                            if (Game.player != null) {
                                Player p = Game.player;
                                java.util.List<Gun> availableGuns = new java.util.ArrayList<>();

                                if (p.rifle != null) availableGuns.add(p.rifle);
                                if (p.shotgun != null) availableGuns.add(p.shotgun);
                                if (p.sniper != null) availableGuns.add(p.sniper);
                                if (p.rocketLauncher != null) availableGuns.add(p.rocketLauncher);

                                if (!availableGuns.isEmpty()) {
                                    Gun selectedGun = availableGuns.get(new java.util.Random().nextInt(availableGuns.size()));

                                    if (selectedGun instanceof Rifle) selectedGun.addAmmo(10);
                                    else if (selectedGun instanceof Shotgun) selectedGun.addAmmo(2);
                                    else if (selectedGun instanceof Sniper) selectedGun.addAmmo(1);
                                    else if (selectedGun instanceof RocketLauncher) selectedGun.addAmmo(1);

                                    System.out.println(selectedGun.getClass().getSimpleName() + " için mermi düştü!");
                                }
                            }
                        }
                    }

                    handler.removeObject(this); 
                    break;
                }
            }
        }
    }


    @Override
    public void render(Graphics g) {
        if (bulletImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            
            int drawX = (int) x;
            int drawY = (int) y;
            int w = 24, h = 24; 
            float centerX = drawX + w / 2f;
            float centerY = drawY + h / 2f;

            //Açı Hesap Algoritması
            double angle = Math.atan2(velY, velX);

            g2d.rotate(angle, centerX, centerY);
            g2d.drawImage(bulletImage, drawX, drawY, w, h, null);
            g2d.dispose();
        } else {
            g.setColor(Color.YELLOW);
            g.fillRect((int)x, (int)y, 8, 8);
        }
    }


    	
   

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x-2, (int)y-2, 12, 12);
    }
}
