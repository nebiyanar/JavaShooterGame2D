
package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



//ENTITYLER OLUSTURULMADAN ONCE TEST İCİN KULLANİLAN BIR CLASS

public class Box extends GameObject {
	private Player player;
	private Camera cam;
	private BufferedImage zombieImage;

	public Box(float x, float y, ID id,Camera cam,Player player) {
		super(x, y, id);
		this.maxHealth = 100;
	    this.health = maxHealth;
	    this.cam=cam;
	    this.player=player;
	    try {
	        File file = new File("src/game/zomb.png"); // Yol doğru olmalı
	        zombieImage = ImageIO.read(file);
	        System.out.println("Zombi resmi yüklendi!");
	    } catch (IOException e) {
	        System.out.println("Zombi resmi yüklenemedi!");
	        e.printStackTrace();
	    }

	
	}

	@Override
	public void tick() {
	    float dx = player.getX() - x;
	    float dy = player.getY() - y;

	    float distance = (float)Math.sqrt(dx*dx + dy*dy);
	    if (distance != 0) {
	        float speed = 1.0f; // zombi hızı
	        velX = (dx / distance) * speed;
	        velY = (dy / distance) * speed;
	    }

	    x += velX;
	    y += velY;

	    // Harita dışına çıkmasın
	    x = Math.max(0, Math.min(x, cam.getWorldWidth() - 32));
	    y = Math.max(0, Math.min(y, cam.getWorldHeight() - 32));
	}

	@Override
	public void render(Graphics g) {
		
		 if (zombieImage != null) {
		        g.drawImage(zombieImage, (int)x, (int)y, 32, 32, null);
		    } else {
		        g.setColor(Color.red);
		        g.fillRect((int)x, (int)y, 32, 32);
		    }
		
		renderHealthBar(g, 32);
		
		
	}
	
	
	public Rectangle getBounds() {
	    return new Rectangle((int)x, (int)y, 32, 32);
	}

	
	
	

}

