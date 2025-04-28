package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player extends GameObject {

    private float _acc = 1f;
    private float _dcc = 0.5f;
    private KeyInput input;
    private int health = 150;
    private Handler handler;
    private Camera cam;
    private int maxHealth = 150;

    private BufferedImage pistolImg, rifleImg, shotgunImg, sniperImg, rocketImg;
    private BufferedImage idleImage;
    private BufferedImage[] runFrames = new BufferedImage[8];

    private int animationIndex = 0;
    private long lastFrameTime = 0;
    private long frameDelay = 100;
    private String direction = "down";

    private long lastDamageTime = 0;
    private long damageCooldown = 1000;

    private Gun currentGun;
    public Gun pistol = new Pistol();
    public Gun rifle = null;
    public Gun shotgun = null;
    public Gun sniper = null;
    public Gun rocketLauncher = null;

    public void heal(int amount) {
        health = Math.min(health + amount, maxHealth);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void increaseMaxHealth(int amount) {
        maxHealth += amount;
        health += amount;
    }

    public Player(float x, float y, ID id, KeyInput input, Handler handler, Camera cam) {
        super(x, y, id);
        this.input = input;
        this.handler = handler;
        this.cam = cam;
        this.currentGun = pistol;
        velX = 0;
        velY = 0;

        try {
            idleImage = ImageIO.read(new File("src/game/idle1.png"));
            for (int i = 0; i < 8; i++) {
                runFrames[i] = ImageIO.read(new File("src/game/run" + (i + 1) + ".png"));
            }
            System.out.println("Tüm sprite'lar başarıyla yüklendi!");
        } catch (IOException e) {
            System.out.println("Sprite yüklenirken hata oluştu!");
        }

        try {
            pistolImg = ImageIO.read(new File("src/game/pistol.png"));
            rifleImg = ImageIO.read(new File("src/game/assault_rifle.png"));
            shotgunImg = ImageIO.read(new File("src/game/shotgun.png"));
            sniperImg = ImageIO.read(new File("src/game/sniper.png"));
            rocketImg = ImageIO.read(new File("src/game/rocketlauncher.png"));
        } catch (IOException e) {
            System.out.println("Silah görselleri yüklenemedi!");
        }
    }

    //silah hizalama
    public float[] getGunTipPosition() {
        float gunX = x;
        float gunY = y;
        int offsetX = 16;
        int offsetY = 16;

        int gunLength = switch (currentGun.getClass().getSimpleName()) {
            case "Pistol" -> 16;
            case "Rifle" -> 20;
            case "Shotgun" -> 18;
            case "Sniper" -> 24;
            case "RocketLauncher" -> 26;
            default -> 16;
        };

        switch (direction) {
            case "right" -> {
                gunX += offsetX + gunLength;
                gunY += offsetY;
            }
            case "left" -> {
                gunX += offsetX - gunLength;
                gunY += offsetY;
            }
            case "up" -> {
                gunX += offsetX;
                gunY += offsetY - gunLength;
            }
            case "down" -> {
                gunX += offsetX;
                gunY += offsetY + gunLength;
            }
        }

        return new float[]{gunX, gunY};
    }

    @Override
    public void tick() {
        Gun gun = getCurrentGun();
        if (gun.isReloading() && System.currentTimeMillis() - gun.reloadStartTime >= gun.reloadDuration) {
            gun.completeReload();
        }

        // Movement için
        if (input.keys[0]) {
            velX += _acc;
            direction = "right";
        } else if (input.keys[1]) {
            velX -= _acc;
            direction = "left";
        } else {
            if (velX > 0) velX -= _dcc;
            else if (velX < 0) velX += _dcc;
        }

        if (input.keys[2]) {
            velY -= _acc;
            direction = "up";
        } else if (input.keys[3]) {
            velY += _acc;
            direction = "down";
        } else {
            if (velY > 0) velY -= _dcc;
            else if (velY < 0) velY += _dcc;
        }

        velX = clamp(velX, 5, -5);
        velY = clamp(velY, 5, -5);

        float nextX = x + velX;
        float nextY = y + velY;
        Rectangle nextBounds = new Rectangle((int) nextX, (int) nextY, 32, 32);

        if (handler.getTileManager() == null || !handler.getTileManager().hasCollision(nextBounds)) {
            x = nextX;
            y = nextY;
        } else {
            velX = 0;
            velY = 0;
        }

        long now = System.currentTimeMillis();
        if (isMoving() && now - lastFrameTime > frameDelay) {
            animationIndex = (animationIndex + 1) % runFrames.length;
            lastFrameTime = now;
        }

        for (GameObject obj : handler.object) {
            if (obj instanceof Zombie z && z.getAttackBounds().intersects(getBounds())) {
                if (now - lastDamageTime >= damageCooldown) {
                    takeDamage(z.damage);
                    lastDamageTime = now;
                }
            }
        }
    }

    private boolean isMoving() {
        return input.keys[0] || input.keys[1] || input.keys[2] || input.keys[3];
    }

    private float clamp(float value, float max, float min) {
        if (value > max) return max;
        else if (value < min) return min;
        return value;
    }

    @Override
    public void render(Graphics g) {
        BufferedImage frame = runFrames[animationIndex];

        if (!isMoving()) {
            g.drawImage(idleImage, (int) x, (int) y, 32, 32, null);
        } else {
            switch (direction) {
                case "right" -> g.drawImage(frame, (int) x, (int) y, 32, 32, null);
                case "left" -> g.drawImage(frame, (int) x + 32, (int) y, -32, 32, null);
                case "up", "down" -> g.drawImage(frame, (int) x, (int) y, 32, 32, null);
            }
        }

        BufferedImage gunImage = null;
        if (currentGun instanceof Pistol) gunImage = pistolImg;
        else if (currentGun instanceof Rifle) gunImage = rifleImg;
        else if (currentGun instanceof Shotgun) gunImage = shotgunImg;
        else if (currentGun instanceof Sniper) gunImage = sniperImg;
        else if (currentGun instanceof RocketLauncher) gunImage = rocketImg;

        if (gunImage != null) {
            int gunWidth = 20, gunHeight = 20;
            int gunOffsetX = 0, gunOffsetY = 0;

            switch (direction) {
                case "right" -> {
                    gunOffsetX = 22;
                    gunOffsetY = 14;
                    g.drawImage(gunImage, (int) x + gunOffsetX, (int) y + gunOffsetY, gunWidth, gunHeight, null);
                }
                case "left" -> {
                    gunOffsetX = -10;
                    gunOffsetY = 14;
                    g.drawImage(gunImage, (int) x + gunOffsetX + gunWidth, (int) y + gunOffsetY, -gunWidth, gunHeight, null);
                }
                case "up", "down" -> {
                    Graphics2D g2d = (Graphics2D) g.create();
                    double angle = direction.equals("up") ? -Math.PI / 2 : Math.PI / 2;
                    gunOffsetX = 6;
                    gunOffsetY = direction.equals("up") ? -5 : 20;
                    int centerX = (int) x + gunOffsetX + gunWidth / 2;
                    int centerY = (int) y + gunOffsetY + gunHeight / 2;
                    g2d.rotate(angle, centerX, centerY);
                    g2d.drawImage(gunImage, (int) x + gunOffsetX, (int) y + gunOffsetY, gunWidth, gunHeight, null);
                    g2d.dispose();
                }
            }
        }

        renderHealthBar(g, 32);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            System.out.println("YOU DIED");
            Game.gameOver = true;
        }
    }

    public Gun getCurrentGun() {
        return currentGun;
    }

    public void setCurrentGun(Gun gun) {
        if (currentGun != null) currentGun.cancelReload();
        this.currentGun = gun;
    }

    public void unlockRifle() {
        if (rifle == null) rifle = new Rifle();
    }

    public void unlockShotgun() {
        if (shotgun == null) shotgun = new Shotgun();
    }

    public void unlockSniper() {
        if (sniper == null) sniper = new Sniper();
    }

    public void unlockRocketLauncher() {
        if (rocketLauncher == null) rocketLauncher = new RocketLauncher();
    }
}
