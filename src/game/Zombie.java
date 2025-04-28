package game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Zombie extends GameObject {
    protected Player player;
    protected Camera cam;
    protected BufferedImage image;
    protected int damage;

    public Zombie(float x, float y, ID id, Camera cam, Player player) {
        super(x, y, id);
        this.player = player;
        this.cam = cam;
    }

    @Override
    public void render(Graphics g) {
        if (image != null) {
            g.drawImage(image, (int) x, (int) y, 32, 32, null);
        } else {
            g.fillRect((int) x, (int) y, 32, 32);
        }

        renderHealthBar(g, 32);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 44, 44);
    }

    public Rectangle getAttackBounds() {
        return new Rectangle((int) x + 16, (int) y + 16, 16, 16);
    }

    protected void followPlayer(float speed) {
        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) return;

        float dirX = dx / distance;
        float dirY = dy / distance;

        float tryX = x + dirX * speed;
        float tryY = y + dirY * speed;
        Rectangle testBounds = new Rectangle((int) tryX, (int) tryY, 32, 32);
        TileManager tm = Game.instance.getHandler().getTileManager();

        if (tm == null) return; //Nullsa hata atmadan returnla

        if (!tm.hasCollision(testBounds)) {
            x = tryX;
            y = tryY;
            return;
        }

        tryX = x + dirX * speed;
        testBounds = new Rectangle((int) tryX, (int) y, 32, 32);
        if (!tm.hasCollision(testBounds)) {
            x = tryX;
            return;
        }

        tryY = y + dirY * speed;
        testBounds = new Rectangle((int) x, (int) tryY, 32, 32);
        if (!tm.hasCollision(testBounds)) {
            y = tryY;
        }
    }

}
