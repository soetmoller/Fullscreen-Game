package moller.resource;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;

import moller.sprites.Sprite;

/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image.
*/
public class TileMap {

    private Image[][] tiles;
    private ArrayList sprites;
    private Sprite player;

    public TileMap(int width, int height) {
        tiles = new Image[width][height];
        sprites = new ArrayList();
    }
    
    public int getWidth() {
        return tiles.length;
    }


    public int getHeight() {
        return tiles[0].length;
    }

    /**
        Gets the tile at the specified location. Returns null if
        no tile is at the location or if the location is out of
        bounds.
    */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= getWidth() ||
            y < 0 || y >= getHeight()) {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }

    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }

    public Sprite getPlayer() {
        return player;
    }

    public void setPlayer(Sprite player) {
        this.player = player;
    }

    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }

    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }

    public Iterator getSprites() {
        return sprites.iterator();
    }

}
