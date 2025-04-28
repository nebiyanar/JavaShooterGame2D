package game;

import java.awt.image.BufferedImage;

public class Camera {
    private float x, y;
    private Handler handler;
    private GameObject tempPlayer = null;

    private int worldWidth, worldHeight;

    public Handler getHandler() {
        return handler;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public Camera(float x, float y, Handler handler, BufferedImage backgroundImage) {
        this.x = x;
        this.y = y;
        this.handler = handler;
        findPlayer();

        
        this.worldWidth = TileManager.WIDTH * TileManager.TILE_SIZE;
        this.worldHeight = TileManager.HEIGHT * TileManager.TILE_SIZE;
    }

    public void findPlayer() {
        for (int i = 0; i < handler.object.size(); i++) {
            if (handler.object.get(i).getId() == ID.Player) {
                tempPlayer = handler.object.get(i);
                break;
            }
        }
    }

    public void tick() {
        if (tempPlayer != null) {
            float newX = tempPlayer.getX() - Game.width / 2 + 16;
            float newY = tempPlayer.getY() - Game.height / 2 + 16;

            x += (newX - x) * 0.1;
            y += (newY - y) * 0.1;
        } else {
            findPlayer();
        }

        //Camera Limitleri
        x = clamp(x, worldWidth - Game.width, 0);
        y = clamp(y, worldHeight - Game.height + TileManager.TILE_SIZE, 0);

    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    
    private float clamp(float value, float max, float min) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
