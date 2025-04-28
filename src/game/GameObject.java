package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GameObject {
	
	protected float x,y;
	protected float velX,velY;
	protected ID id;
	protected int health;
	protected int maxHealth;

	public GameObject(float x,float y,ID id) {
	this.x=x;
	this.y=y;
	this.id=id;
	}
	public abstract Rectangle getBounds();
	public abstract void tick();
	public abstract void render(Graphics g);
	
	public void renderHealthBar(Graphics g, int width) {
	    g.setColor(Color.green);
	    int barWidth = (int)(width * ((float)health / maxHealth));
	    g.fillRect((int)x, (int)y - 10, barWidth, 5);
	    
	    if(health<= maxHealth*0.5) {
	    	g.setColor(Color.red);
	    	 g.fillRect((int)x, (int)y - 10, barWidth, 5);
	    }
	}


	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getVelX() {
		return velX;
	}

	public void setVelX(float velX) {
		this.velX = velX;
	}

	public float getVelY() {
		return velY;
	}

	public void setVelY(float velY) {
		this.velY = velY;
	}

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}
	
	

}
