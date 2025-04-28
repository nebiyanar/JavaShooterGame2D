package game;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TankZombie extends Zombie {
    public TankZombie(float x, float y, Camera cam, Player player) {
        super(x, y, ID.Zombie, cam, player);
        this.maxHealth = 300;
        this.health = maxHealth;
        this.damage = 60;

        try {
            image = ImageIO.read(new File("src/game/zomb1.4.png"));
        } catch (IOException e) {
            System.out.println("Tank zombi resmi yüklenemedi!");
        }
    }

    @Override
    public void tick() {
        float dx = player.getX() + 16 - x;
        float dy = player.getY() + 16 - y;
        followPlayer(0.5f); // çok yavaş
    }
}