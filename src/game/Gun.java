package game;

public abstract class Gun {
    protected int magazineSize;
    protected int currentAmmo;
    protected int reserveAmmo;
    protected int fireRate; // mermi/dk
    protected long lastFiredTime = 0;
    protected boolean isReloading = false;
    protected long reloadStartTime = 0;
    protected long reloadDuration = 2000; // 2 sn
    protected int reloadPerBullet = 300; // 0.3 sn

    public void setCurrentAmmo(int ammo) {
        this.currentAmmo = ammo;
    }

    public void setReserveAmmo(int ammo) {
        this.reserveAmmo = ammo;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public float getReloadProgress() {
        if (!isReloading) return 0;
        return Math.min(1f, (System.currentTimeMillis() - reloadStartTime) / (float)reloadDuration);
    }

    public Gun(int magazineSize, int fireRate, int reserveAmmo) {
        this.magazineSize = magazineSize;
        this.fireRate = fireRate;
        this.reserveAmmo = reserveAmmo;
        this.currentAmmo = magazineSize;
    }

    public void addAmmo(int amount) {
        reserveAmmo += amount;
    }

    public boolean canFire() {
        long currentTime = System.currentTimeMillis();
        return currentAmmo > 0 && currentTime - lastFiredTime >= (60000 / fireRate) && !isReloading;
    }

    public void reload() {
        if (currentAmmo == magazineSize || reserveAmmo == 0 || isReloading) return;

        int needed = magazineSize - currentAmmo;
        int toReload = Math.min(needed, reserveAmmo);

        isReloading = true;
        reloadStartTime = System.currentTimeMillis();
        reloadDuration = Math.min(toReload * reloadPerBullet, 2500); 
    }

    
    
    public void cancelReload() {
        isReloading = false;
        reloadStartTime = 0;
        reloadDuration = 0;
    }



    public void completeReload() {
        int needed = magazineSize - currentAmmo;
        int toReload = Math.min(needed, reserveAmmo);
        currentAmmo += toReload;
        reserveAmmo -= toReload;
        isReloading = false;
    }

    public abstract void fire(float x, float y, float angle, Handler handler);

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public int getReserveAmmo() {
        return reserveAmmo;
    }
}