package game;

import java.awt.Point;
import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseInput extends MouseAdapter {

    private Handler handler;
    private Camera cam;
    private GameObject tempPlayer = null;
    private boolean shooting = false;

    public MouseInput(Handler handler, Camera cam) {
        this.handler = handler;
        this.cam = cam;
    }

    public void findPlayer() {
        for (int i = 0; i < handler.object.size(); i++) {
            if (handler.object.get(i).getId() == ID.Player) {
                tempPlayer = handler.object.get(i);
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        shooting = true;

        int mx = e.getX();
        int my = e.getY();
        Point click = new Point(mx, my);

        // === Ana Menü ===
        if (Game.instance.openMainMenu) {
            if (Game.instance.startButton.contains(click)) {
                Game.instance.openMainMenu = false;
                Game.instance.showDifficultyMenu = true;
            } else if (Game.instance.loadButton.contains(click)) {
                Game.instance.loadGame();
            } else if (Game.instance.exitButton.contains(click)) {
                System.exit(0);
            } else if (Game.instance.musicButton.contains(click)) {
                Game.instance.musicOn = !Game.instance.musicOn;

                if (Game.instance.musicOn) {
                    if (Game.instance.backgroundMusic != null) {
                        Game.instance.backgroundMusic.start();
                    } else {
                        Game.instance.initMusic(); 
                    }
                } else {
                    if (Game.instance.backgroundMusic != null) {
                        Game.instance.backgroundMusic.stop();
                    }
                }
            }
            return;
        }

        // Difficulty sec
        if (Game.instance.showDifficultyMenu) {
            if (Game.instance.easyButton.contains(click)) {
                Game.difficulty = Difficulty.EASY;
                Game.instance.startGame();
            } else if (Game.instance.mediumButton.contains(click)) {
                Game.difficulty = Difficulty.MEDIUM;
                Game.instance.startGame();
            } else if (Game.instance.hardButton.contains(click)) {
                Game.difficulty = Difficulty.HARD;
                Game.instance.startGame();
            } else if (Game.instance.testButton.contains(click)) {
                Game.difficulty = Difficulty.TEST;
                Game.instance.startGame();
            }
            return;
        }

      
    }



    @Override
    public void mouseReleased(MouseEvent e) {
        shooting = false;
    }

    public void tick() {
        if (tempPlayer == null) findPlayer();

        if (!shooting || tempPlayer == null) return;

        Player p = (Player) tempPlayer;
        Gun gun = p.getCurrentGun();

        if (gun.isReloading()) {
            gun.cancelReload();  // Önce reload'u iptal et
        }

        if (gun.canFire()) {
            try {
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                Point gamePos = Game.instance.getLocationOnScreen();

                int mx = mousePos.x - gamePos.x;
                int my = mousePos.y - gamePos.y;

                float[] gunTip = p.getGunTipPosition();
                float dx = mx - (gunTip[0] - cam.getX());
                float dy = my - (gunTip[1] - cam.getY());
                float angle = (float) Math.atan2(dy, dx);

                gun.fire(gunTip[0], gunTip[1], angle, handler);
            } catch (Exception e) {
               
            }
        }
    }

}
