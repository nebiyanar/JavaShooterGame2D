package game;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TileManager {
    public static final int TILE_SIZE = 32;
    public static final int WIDTH = 40;
    public static final int HEIGHT = 30;
    private Tile[][] tiles = new Tile[WIDTH][HEIGHT];
    private Set<Point> occupied = new HashSet<>();

    public enum TileType {
        GRASS("grass.png", false),
        BUSH("bush.png", false),
        TREE("tree.png", true),
        HAYBALE("haybale.png", true), // mapte çok collision alan olacagi icin
        BRICK("brick.png", true),     // kullanilmadi
        STONE("stone.png", true),
        LAVA("lava.png", false),
        WATER("water.png", false);

        public final String filename;
        public final boolean hasCollision;

        TileType(String filename, boolean hasCollision) {
            this.filename = filename;
            this.hasCollision = hasCollision;
        }
    }

    public static class Tile {
        TileType type;
        BufferedImage image;

        Tile(TileType type) {
            this.type = type;
            try {
                image = ImageIO.read(new File("src/game/" + type.filename));
            } catch (IOException e) {
                System.out.println("Görsel yüklenemedi: " + type.filename);
            }
        }

        void render(Graphics g, int x, int y) {
            if (image != null)
                g.drawImage(image, x, y, TILE_SIZE, TILE_SIZE, null);
        }

        boolean hasCollision() {
            return type.hasCollision;
        }

        TileType getType() {
            return type;
        }
    }

    public TileManager() {
        generateMap();
    }

    public void generateMap() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                setTile(x, y, TileType.GRASS);
            }
        }

        for (int x = 0; x < WIDTH; x++) {
            setTile(x, 0, TileType.BRICK);
            setTile(x, HEIGHT - 1, TileType.BRICK);
        }
        for (int y = 0; y < HEIGHT; y++) {
            setTile(0, y, TileType.BRICK);
            setTile(WIDTH - 1, y, TileType.BRICK);
        }

        int wx = 2, wy = 2;
        placeArea(wx, wy, TileType.WATER);
        surroundArea(wx, wy, TileType.STONE);

        int lx = WIDTH - 5, ly = HEIGHT - 5;
        placeArea(lx, ly, TileType.LAVA);
        surroundArea(lx, ly, TileType.STONE);

        Random rand = new Random();
        int treeCount = 60;
        int bushCount = 100;

        for (int i = 0; i < treeCount;) {
            int x = rand.nextInt(WIDTH - 2) + 1;
            int y = rand.nextInt(HEIGHT - 2) + 1;
            if (canPlaceTree(x, y)) {
                setTile(x, y, TileType.TREE);
                markSurroundingOccupied(x, y);
                i++;
            }
        }

        for (int i = 0; i < bushCount;) {
            int x = rand.nextInt(WIDTH - 2) + 1;
            int y = rand.nextInt(HEIGHT - 2) + 1;
            Point p = new Point(x, y);
            if (!occupied.contains(p) && !isSpecialTile(x, y)) {
                setTile(x, y, TileType.BUSH);
                i++;
            }
        }
    }

    private boolean canPlaceTree(int x, int y) {
        if (isSpecialTile(x, y)) return false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Point p = new Point(x + dx, y + dy);
                if (occupied.contains(p)) return false;
            }
        }
        return true;
    }

    private void markSurroundingOccupied(int cx, int cy) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = cx + dx;
                int y = cy + dy;
                if (x > 0 && y > 0 && x < WIDTH && y < HEIGHT) {
                    occupied.add(new Point(x, y));
                }
            }
        }
    }

    private void placeArea(int startX, int startY, TileType type) {
        for (int x = startX; x < startX + 2; x++) {
            for (int y = startY; y < startY + 2; y++) {
                setTile(x, y, type);
                occupied.add(new Point(x, y));
            }
        }
    }

    private void surroundArea(int startX, int startY, TileType type) {
        for (int x = startX - 1; x <= startX + 2; x++) {
            for (int y = startY - 1; y <= startY + 2; y++) {
                if (x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT) {
                    if (!occupied.contains(new Point(x, y)))
                        setTile(x, y, type);
                }
            }
        }
    }

    private boolean isSpecialTile(int x, int y) {
        TileType t = getTileTypeAt(x, y);
        return t == TileType.WATER || t == TileType.LAVA || t == TileType.STONE || t == TileType.BRICK;
    }

    public void setTile(int x, int y, TileType type) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            tiles[x][y] = new Tile(type);
        }
    }

    public void render(Graphics g) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y].render(g, x * TILE_SIZE, y * TILE_SIZE);
            }
        }
    }

    public boolean hasCollision(float x, float y) {
        int tileX = (int)(x / TILE_SIZE);
        int tileY = (int)(y / TILE_SIZE);
        if (tileX < 0 || tileY < 0 || tileX >= WIDTH || tileY >= HEIGHT) return true;
        return tiles[tileX][tileY].hasCollision();
    }

    public boolean hasCollision(Rectangle bounds) {
        int left = bounds.x;
        int right = bounds.x + bounds.width;
        int top = bounds.y;
        int bottom = bounds.y + bounds.height;

        return hasCollision(left, top) || hasCollision(right, top) ||
               hasCollision(left, bottom) || hasCollision(right, bottom);
    }

    public TileType getTileTypeAt(int x, int y) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            return tiles[x][y].type;
        }
        return TileType.GRASS;
    }

    // iyi bir spawn yeri mi
    public boolean isSpawnable(Rectangle rect) {
        int left = rect.x / TILE_SIZE;
        int top = rect.y / TILE_SIZE;
        int right = (rect.x + rect.width) / TILE_SIZE;
        int bottom = (rect.y + rect.height) / TILE_SIZE;

        for (int x = left; x <= right; x++) {
            for (int y = top; y <= bottom; y++) {
                if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) return false;
                Tile tile = tiles[x][y];
                TileType type = tile.getType();
                if (type == TileType.LAVA || type == TileType.WATER || type == TileType.BRICK || type == TileType.STONE || tile.hasCollision()) {
                    return false;
                }
            }
        }
        return true;
    }
}
