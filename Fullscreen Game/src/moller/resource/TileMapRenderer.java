package moller.resource;

import java.awt.*;
import java.util.Iterator;

import moller.sprites.Creature;
import moller.sprites.Sprite;

/**
 * The TileMapRenderer class draws a TileMap on the screen. It draws all tiles,
 * sprites, and an optional background image centered around the position of the
 * player.
 * 
 * If the width of background image is smaller the width of the tile map, the
 * background image will appear to move slowly, creating a parallax background
 * effect.
 * 
 * Three static methods are provided to convert pixels to tile positions,
 * and vice-versa.
 */
public class TileMapRenderer {

	public static final int TILE_SIZE = 64;
	public static final int TILE_SIZE_BITS = 6;

	private Image background;
	private int screenWidth, screenHeight, mapWidth;
	private int offsetX, offsetY;
	private TileMap tileMap;

	public static int pixelsToTiles(float pixels) {
		return pixelsToTiles(Math.round(pixels));
	}

	public static int pixelsToTiles(int pixels) {
		return (int) Math.floor(pixels / TILE_SIZE);
	}

	public static int tilesToPixels(int numTiles) {
		return numTiles * TILE_SIZE;
	}

	public void setBackground(Image background) {
		this.background = background;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public TileMap getTileMap() {
		return tileMap;
	}

	public void setTileMap(TileMap tileMap) {
		this.tileMap = tileMap;
		mapWidth = tilesToPixels(tileMap.getWidth());
	}
	
	public void draw(Graphics2D g) {
		Sprite player = tileMap.getPlayer();
		setupDrawingVariables(player);
		drawBackground(g);
		drawVisibleTiles(g);
		drawPlayer(g, player);
		drawSprites(g);
	}

	public void setupDrawingVariables(Sprite player) {
		calculateOffsetX(player);
		calculateOffsetY();
	}
	
	private void calculateOffsetX(Sprite player) {
		offsetX = screenWidth / 2 - Math.round(player.getX()) - TILE_SIZE;
		offsetX = Math.min(offsetX, 0);
		offsetX = Math.max(offsetX, screenWidth - mapWidth);
	}

	private void calculateOffsetY() {
		offsetY = screenHeight - tilesToPixels(tileMap.getHeight());
	}
	
	// Draws a parallax background image and fills it with black if needed.
	private void drawBackground(Graphics2D g) {
		if (background == null || screenHeight > background.getHeight(null)) {
			g.setColor(Color.black);
			g.fillRect(0, 0, screenWidth, screenHeight);
		}
		if (background != null) {
			int x = offsetX * (screenWidth - background.getWidth(null))
					/ (screenWidth - mapWidth);
			int y = screenHeight - background.getHeight(null);

			g.drawImage(background, x, y, null);
		}
	}
	
	private void drawVisibleTiles(Graphics2D g) {
		int firstTileX = pixelsToTiles(-offsetX);
		int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
		for (int y = 0; y < tileMap.getHeight(); y++) {
			for (int x = firstTileX; x <= lastTileX; x++) {
				Image image = tileMap.getTile(x, y);
				if (image != null) {
					g.drawImage(image, tilesToPixels(x) + offsetX,
							tilesToPixels(y) + offsetY, null);
				}
			}
		}
	}
	
	private void drawPlayer(Graphics2D g, Sprite player) {
		g.drawImage(player.getImage(), Math.round(player.getX()) + offsetX,
				Math.round(player.getY()) + offsetY, null);
	}

	private void drawSprites(Graphics2D g) {
		Iterator i = tileMap.getSprites();
		while (i.hasNext()) {
			Sprite sprite = (Sprite) i.next();
			int x = Math.round(sprite.getX()) + offsetX;
			int y = Math.round(sprite.getY()) + offsetY;
			g.drawImage(sprite.getImage(), x, y, null);

			// wake up the creature when it's on screen
			if (sprite instanceof Creature && x >= 0 && x < screenWidth) {
				((Creature) sprite).wakeUp();
			}
		}
	}
	
	private void drawBullets(Graphics2D g) {
		
	}

}
