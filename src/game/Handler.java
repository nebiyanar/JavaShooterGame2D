package game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.LinkedList;

public class Handler {

    public LinkedList<GameObject> object = new LinkedList<>();
    private TileManager tileManager;

    public void setTileManager(TileManager tileManager) {
        this.tileManager = tileManager;
    }

    public TileManager getTileManager() {
        return tileManager;
    }

    public void tick() {
        for (int i = 0; i < object.size(); i++) {
            GameObject obj = object.get(i);

            obj.tick();

            // Collision checkliyoruz burda ve aşşağılarda
            if (tileManager != null && obj.getId() != ID.Bullet) {
                if (tileManager.hasCollision(obj.getBounds())) {
                    //Algoritma 
                    obj.setX(obj.getX() - obj.velX);
                    obj.setY(obj.getY() - obj.velY);
                    obj.velX = 0;
                    obj.velY = 0;
                }
            }
        }
    }

    public int zombieCount() {
        int count = 0;
        for (GameObject obj : object) {
            if (obj instanceof Zombie) count++;
        }
        return count;
    }

    public void render(Graphics g) {
        for (int i = 0; i < object.size(); i++) {
            object.get(i).render(g);
        }

        
        for (int i = 0; i < object.size(); i++) {
            GameObject obj = object.get(i);
            if (obj.getId() == ID.Bullet) {
                Rectangle bulletBounds = obj.getBounds();
                for (int j = 0; j < object.size(); j++) {
                    GameObject obj2 = object.get(j);
                    if (obj2.getId() == ID.Block) {
                        Rectangle boxBounds = obj2.getBounds();
                        if (bulletBounds.intersects(boxBounds)) {
                            Box box = (Box) obj2;
                            box.health -= 50;
                            removeObject(obj); 
                            if (box.health <= 0) {
                                removeObject(box);
                                Game.killedBoxes++;
                            }
                            break;
                        }
                    }
                }

                
                if (tileManager != null && tileManager.hasCollision(bulletBounds)) {
                    removeObject(obj); 
                }
            }
        }
    }

    public GameObject addObject(GameObject tempobject) {
        object.add(tempobject);
        return tempobject;
    }

    public void removeObject(GameObject tempObject) {
        object.remove(tempObject);
    }
}
