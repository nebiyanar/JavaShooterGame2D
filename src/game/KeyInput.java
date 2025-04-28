package game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
    private Player player;
    public boolean keys[] = new boolean[4]; // D, A, W, S

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
     // kontrol için   System.out.println("Tuş basıldı: " + key); 

        // Save Alma
        if (key == KeyEvent.VK_M) {
            Game.instance.saveGame();
            Game.instance.showSaveMenu = true;
            Game.instance.saveMenuStartTime = System.currentTimeMillis();
           // System.out.println("M başarili");
            return;
        }

        // F Music Kapa aç
        if (key == KeyEvent.VK_F) {
            Game.instance.musicOn = !Game.instance.musicOn;
            if (Game.instance.backgroundMusic != null) {
                if (Game.instance.musicOn) {
                    Game.instance.backgroundMusic.start();
                //    System.out.println("Music Acildi);
                } else {
                    Game.instance.backgroundMusic.stop();
                    //System.out.println(" Music Kapandi");
                }
            }
            return;
        }

        // Heal Menü
     // Heal Menü
        if (Game.openHealthMenu) {
            if (player == null) return;

            switch (key) {
                case KeyEvent.VK_K -> {
                    if ((Game.score - Game.spentScore) >= 100 && player.getHealth() < player.getMaxHealth()) {
                        player.heal(20);
                        Game.spentScore += 100;
                    }
                }
                case KeyEvent.VK_L -> {
                    if ((Game.score - Game.spentScore) >= 250) {
                        player.increaseMaxHealth(20);
                        Game.spentScore += 250;
                    }
                }
                case KeyEvent.VK_U -> {
                    if ((Game.score - Game.spentScore) >= 150 && player.getHealth() < player.getMaxHealth()) {
                        player.heal(player.getMaxHealth() - player.getHealth());
                        Game.spentScore += 150;
                    }
                }
                case KeyEvent.VK_O -> {
                    Game.openHealthMenu = false;
                    Game.instance.togglePause();
                }
            }
            return;
        }


        // Movement
        if (key == KeyEvent.VK_D) keys[0] = true;
        if (key == KeyEvent.VK_A) keys[1] = true;
        if (key == KeyEvent.VK_W) keys[2] = true;
        if (key == KeyEvent.VK_S) keys[3] = true;

        // Silah degisimi ve reload
        if (player != null) {
            switch (key) {
                case KeyEvent.VK_1 -> player.setCurrentGun(player.pistol);
                case KeyEvent.VK_2 -> {
                    if (player.rifle != null) player.setCurrentGun(player.rifle);
                }
                case KeyEvent.VK_3 -> {
                    if (player.shotgun != null) player.setCurrentGun(player.shotgun);
                }
                case KeyEvent.VK_4 -> {
                    if (player.sniper != null) player.setCurrentGun(player.sniper);
                }
                case KeyEvent.VK_5 -> {
                    if (player.rocketLauncher != null) player.setCurrentGun(player.rocketLauncher);
                }
                case KeyEvent.VK_R -> {
                    Gun gun = player.getCurrentGun();
                    if (gun.isReloading()) {
                        gun.cancelReload();
                      //  System.out.println(" Reload iptal ");
                    } else {
                        gun.reload();
                        //System.out.println(" Reload başladı.");
                    }
                }
                case KeyEvent.VK_H -> {
                    Game.openHealthMenu = true;
                    Game.instance.togglePause();
                    //System.out.println("Health Menu Acildi");
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_D) keys[0] = false;
        if (key == KeyEvent.VK_A) keys[1] = false;
        if (key == KeyEvent.VK_W) keys[2] = false;
        if (key == KeyEvent.VK_S) keys[3] = false;
    }
}
