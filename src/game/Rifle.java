package game;

import java.util.Random;

public class Rifle extends Gun {
    private Random rand = new Random();

    public Rifle() {
        super(30, 600, 0); // 30 elde 600 hızında
        this.reloadPerBullet=100;
    }

    @Override
    public void fire(float x, float y, float angle, Handler handler) {
        if (!canFire()) return;

        
        float deviation = (float) Math.toRadians(rand.nextFloat() * 30 - 15);
        float finalAngle = angle + deviation;

        Bullet bullet = new Bullet(x, y, ID.Bullet,handler);
        bullet.velX = (float) (10 * Math.cos(finalAngle));
        bullet.velY = (float) (10 * Math.sin(finalAngle));
        handler.addObject(bullet);

        currentAmmo--;
        lastFiredTime = System.currentTimeMillis();
    }
}
