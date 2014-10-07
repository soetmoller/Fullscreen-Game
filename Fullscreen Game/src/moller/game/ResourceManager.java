package moller.game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import moller.resource.TileMap;
import moller.resource.TileMapRenderer;
import moller.sprites.Animation;
import moller.sprites.Enemy;
import moller.sprites.Item;
import moller.sprites.Player;
import moller.sprites.Sprite;

/**
 * The ResourceManager class loads and manages tile Images and "host" Sprites
 * used in the game. Game Sprites are cloned from "host" Sprites.
 */
public class ResourceManager {

	private ArrayList tiles;
	private int currentLevel;
	private GraphicsConfiguration gc;

	// host sprites used for cloning
	private Sprite playerSprite;
	private Sprite coinSprite;
	private Sprite ammoSprite;
	private Sprite goalSprite;
	private Sprite flyingSprite;
	private Sprite blobSprite;

	/**
	 * Creates a new ResourceManager with the specified GraphicsConfiguration.
	 */
	public ResourceManager(GraphicsConfiguration gc) {
		this.gc = gc;
		loadTileImages();
		loadCreatureSprites();
		loadItemSprites();
	}

	/**
	 * Gets an image from the images/ directory.
	 */
	public Image loadImage(String name) {
		String filename = "Images/" + name;
		return new ImageIcon(filename).getImage();
	}

	public Image getMirrorImage(Image image) {
		return getScaledImage(image, -1, 1);
	}

	public Image getFlippedImage(Image image) {
		return getScaledImage(image, 1, -1);
	}

	private Image getScaledImage(Image image, float x, float y) {

		// set up the transform
		AffineTransform transform = new AffineTransform();
		transform.scale(x, y);
		transform.translate((x - 1) * image.getWidth(null) / 2,
				(y - 1) * image.getHeight(null) / 2);

		// create a transparent image
		Image newImage = gc.createCompatibleImage(image.getWidth(null),
				image.getHeight(null), Transparency.BITMASK);

		// draw the transformed image
		Graphics2D g = (Graphics2D) newImage.getGraphics();
		g.drawImage(image, transform, null);
		g.dispose();

		return newImage;
	}

	public TileMap loadNextLevel() {
		TileMap map = null;
		while (map == null) {
			currentLevel++;
			try {
				map = loadLevel("Levels/Level" + currentLevel + ".txt");
			} catch (IOException ex) {
				if (currentLevel == 1) {
					// no maps to load!
					return null;
				}
				currentLevel = 0;
				map = null;
			}
		}

		return map;
	}

	public TileMap reloadLevel() {
		try {
			return loadLevel("Levels/Level" + currentLevel + ".txt");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private TileMap loadLevel(String filename) throws IOException {
		ArrayList lines = new ArrayList();
		int width = 0;
		int height = 0;

		// read every line in the text file into the list
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				reader.close();
				break;
			}

			// add every line except for comments
			if (!line.startsWith("#")) {
				lines.add(line);
				width = Math.max(width, line.length());
			}
		}

		height = lines.size();
		TileMap newMap = new TileMap(width, height);
		for (int y = 0; y < height; y++) {
			String line = (String) lines.get(y);
			for (int x = 0; x < line.length(); x++) {
				char ch = line.charAt(x);

				if (ch == 'g') {
					newMap.setTile(x, y, (Image) tiles.get(0));
				}

				// check if the char represents a sprite
				else if (ch == 'b') {
					addSprite(newMap, ammoSprite, x, y);
				} else if (ch == 'c') {
					addSprite(newMap, coinSprite, x, y);
				} else if (ch == 'm') {
					addSprite(newMap, goalSprite, x, y);
				} else if (ch == '1') {
					addSprite(newMap, flyingSprite, x, y);
				} else if (ch == 'e') {
					addSprite(newMap, blobSprite, x, y);
				}
			}
		}

		// add the player to the map
		Sprite player = (Sprite) playerSprite.clone();
		player.setX(TileMapRenderer.tilesToPixels(3));
		player.setY(0);
		newMap.setPlayer(player);

		return newMap;
	}

	private void addSprite(TileMap map, Sprite hostSprite, int tileX, int tileY) {
		if (hostSprite != null) {
			// clone the sprite from the "host"
			Sprite sprite = (Sprite) hostSprite.clone();
			// center the sprite
			sprite.setX(TileMapRenderer.tilesToPixels(tileX)
					+ (TileMapRenderer.tilesToPixels(1) - sprite.getWidth())
					/ 2);

			// bottom-justify the sprite
			sprite.setY(TileMapRenderer.tilesToPixels(tileY + 1)
					- sprite.getHeight());

			// add it to the map
			map.addSprite(sprite);
		}
	}

	public void loadTileImages() {
		tiles = new ArrayList();
		char ch = 'g';
		// keep looking for tile g,h,i,...
		// makes it easier to add new tile images.
		while (true) {
			String name = "tile_" + ch + ".png";
			File file = new File("Images/" + name);
			if (!file.exists()) {
				break;
			}
			tiles.add(loadImage(name));
			ch++;
		}
	}

	public void loadCreatureSprites() {
		Image[][] images = new Image[4][];

		// load left-facing images
		images[0] = new Image[] { loadImage("player1.png"),
				loadImage("player2.png"), loadImage("player3.png"),
				loadImage("blob1.png"), loadImage("blob2.png"),
				loadImage("blob3.png"), };

		images[1] = new Image[images[0].length];
		images[2] = new Image[images[0].length];
		images[3] = new Image[images[0].length];
		for (int i = 0; i < images[0].length; i++) {
			// right-facing images
			images[1][i] = getMirrorImage(images[0][i]);
			// left-facing "dead" images
			images[2][i] = getFlippedImage(images[0][i]);
			// right-facing "dead" images
			images[3][i] = getFlippedImage(images[0][i]);
		}

		// create creature animations
		Animation[] playerAnim = new Animation[4];
		Animation[] blobAnim = new Animation[4];
		for (int i = 0; i < 4; i++) {
			playerAnim[i] = createPlayerAnim(images[i][0], images[i][1],
					images[i][2]);
			blobAnim[i] = createBlobAnim(images[i][3], images[i][4],
					images[i][5]);
		}

		// create creature sprites
		playerSprite = new Player(playerAnim[0], playerAnim[1], playerAnim[2],
				playerAnim[3]);
		blobSprite = new Enemy.Blob(blobAnim[0], blobAnim[1], blobAnim[2],
				blobAnim[3]);

	}

	private Animation createPlayerAnim(Image player1, Image player2,
			Image player3) {
		Animation anim = new Animation();
		anim.addScene(player1, 250);
		anim.addScene(player2, 150);
		anim.addScene(player1, 150);
		anim.addScene(player2, 150);
		anim.addScene(player3, 200);
		anim.addScene(player2, 150);
		return anim;
	}

	private Animation createBlobAnim(Image img1, Image img2, Image img3) {
		Animation anim = new Animation();
		anim.addScene(img1, 50);
		anim.addScene(img2, 50);
		anim.addScene(img3, 50);
		anim.addScene(img2, 50);
		return anim;
	}

	private void loadItemSprites() {
		// create "goal" sprite
		Animation anim = new Animation();
		anim.addScene(loadImage("goal1.png"), 150); // pic1
		anim.addScene(loadImage("goal2.png"), 150); // pic2
		anim.addScene(loadImage("goal3.png"), 150); // pic3
		anim.addScene(loadImage("goal2.png"), 150); // pic2
		goalSprite = new Item.Goal(anim);

		// create "bullet" sprite
		anim = new Animation();
		anim.addScene(loadImage("ammo1.png"), 100); // pic1
		anim.addScene(loadImage("ammo2.png"), 100); // pic2
		anim.addScene(loadImage("ammo3.png"), 100); // pic3
		anim.addScene(loadImage("ammo4.png"), 100); // pic2
		ammoSprite = new Item.Ammo(anim);

		// create "coin" sprite
		anim = new Animation();
		anim.addScene(loadImage("coin1.png"), 150); // pic1
		anim.addScene(loadImage("coin2.png"), 150); // pic2
		anim.addScene(loadImage("coin3.png"), 150); // pic3
		anim.addScene(loadImage("coin2.png"), 150); // pic2
		coinSprite = new Item.Coin(anim);
	}
}
