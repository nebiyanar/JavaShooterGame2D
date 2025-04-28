package game;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class NormalZombie extends Zombie {
    public NormalZombie(float x, float y, Camera cam, Player player) {
        super(x, y, ID.Zombie, cam, player);
        this.maxHealth = 100;
        this.health = maxHealth;
        this.damage = 30;

        try {
            image = ImageIO.read(new File("src/game/zomb1.3.png"));
        } catch (IOException e) {
            System.out.println("Normal zombi resmi yüklenemedi!");
        }
    }

    @Override
    public void tick() {
        float dx = player.getX() + 16 - x;
        float dy = player.getY() + 16 - y;
        followPlayer(1.0f); // yavaş
    }
}