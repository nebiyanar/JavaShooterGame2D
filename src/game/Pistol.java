package game;

public class Pistol extends Gun {
    public Pistol() {
        super(12, 120, Integer.MAX_VALUE); // sınırsız yedek şarjör
        this.reloadPerBullet=150;
    }
    

    @Override
    public void fire(float x, float y, float angle, Handler handler) {
        if (!canFire()) return;

        Bullet bullet = new Bullet(x, y, ID.Bullet,handler);
        bullet.velX = (float) (10 * Math.cos(angle));
        bullet.velY = (float) (10 * Math.sin(angle));
        handler.addObject(bullet);

        currentAmmo--;
        lastFiredTime = System.currentTimeMillis();
    }
}
