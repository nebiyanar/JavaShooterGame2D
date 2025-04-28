package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CrawlerZombie extends Zombie {
    private boolean leaped = false;
    private float angle = 0; //radyan

    public CrawlerZombie(float x, float y, Camera cam, Player player) {
        super(x, y, ID.Zombie, cam, player);
        this.maxHealth = 60;
        this.health = maxHealth;
        this.damage = 30;

        try {
            image = ImageIO.read(new File("src/game/crawl zombie.png"));
        } catch (IOException e) {
            System.out.println("Sürünge zombi resmi yüklenemedi!");
        }
    }
  
    
    @Override
    public void render(Graphics g) {
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            float dx = player.getX() - x;
            float dy = player.getY() - y;

            int drawX = (int) x;
            int drawY = (int) y;
            int width = 32;
            int height = 32;

            
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    //Sağ
                    g2d.drawImage(image, drawX, drawY, width, height, null);
                } else {
                    // Sol
                    g2d.drawImage(image, drawX + width, drawY, -width, height, null);
                }
            } else {
                if (dy > 0) {
                    // Aşağı
                    g2d.rotate(Math.PI / 2, drawX + width / 2.0, drawY + height / 2.0);
                    g2d.drawImage(image, drawX, drawY, width, height, null);
                } else {
                    // Yukarı
                    g2d.rotate(-Math.PI / 2, drawX + width / 2.0, drawY + height / 2.0);
                    g2d.drawImage(image, drawX, drawY, width, height, null);
                }
            }

            g2d.dispose();
        } else {
            g.setColor(Color.GREEN);
            g.fillRect((int) x, (int) y, 32, 32);
        }

        renderHealthBar(g, 32);
    }

    @Override
    public void tick() {
        float dx = player.getX() + 16 - x;
        float dy = player.getY() + 16 - y;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);

        // Eğer 1.5 tile (48px) veya daha yakınsa ve henüz atlamadıysa
        if (distance <= 48 && !leaped) { //jump özelliği yazıldı
        	//System.out.println("atladi");
            float boost = 6.0f; // zıplama hızı jump için
            velX = dx / distance * boost;
            velY = dy / distance * boost;
            x += velX;
            y += velY;
            leaped = true;
        } else {
            followPlayer(2.0f); // normal hiz
        }
    }

}