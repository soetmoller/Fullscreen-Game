package moller.core;

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
 * used in the game.
 */
public class ResourceManager {

	public static final int DEFAULT_STARTING_X = 3;
	public static final int DEFAULT_STARTING_Y = 0;

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
	
	private TileMap currentMap;

	/**
	 * Creates a new ResourceManager with the specified GraphicsConfiguration.
	 */
	public ResourceManager(GraphicsConfiguration gc) {
		this.gc = gc;
		loadTileImages();
		loadCreatureSprites();
		loadItemSprites();
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
		Animation[] playerAnimation = new Animation[4];
		Animation[] blobAnimation = new Animation[4];
		for (int i = 0; i < 4; i++) {
			playerAnimation[i] = createPlayerAnim(images[i][0], images[i][1],
					images[i][2]);
			blobAnimation[i] = createBlobAnim(images[i][3], images[i][4],
					images[i][5]);
		}
		createCreatureSprites(playerAnimation, blobAnimation);
	}
	
	private void createCreatureSprites(Animation[] playerAnimation, Animation[] blobAnimation) {
		playerSprite = new Player(playerAnimation[0], playerAnimation[1], playerAnimation[2],
				playerAnimation[3]);
		blobSprite = new Enemy.Blob(blobAnimation[0], blobAnimation[1], blobAnimation[2],
				blobAnimation[3]);
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
		loadGoalAnimations();
		loadAmmoAnimation();
		loadCoinAnimation();
	}
	
	private void loadGoalAnimations() {
		Animation anim = new Animation();
		anim.addScene(loadImage("goal1.png"), 150); // pic1
		anim.addScene(loadImage("goal2.png"), 150); // pic2
		anim.addScene(loadImage("goal3.png"), 150); // pic3
		anim.addScene(loadImage("goal2.png"), 150); // pic2
		goalSprite = new Item.Goal(anim);
	}
	
	private void loadAmmoAnimation() {
		Animation anim = new Animation();
		anim.addScene(loadImage("ammo1.png"), 100); // pic1
		anim.addScene(loadImage("ammo2.png"), 100); // pic2
		anim.addScene(loadImage("ammo3.png"), 100); // pic3
		anim.addScene(loadImage("ammo4.png"), 100); // pic2
		ammoSprite = new Item.Ammo(anim);
	}
	
	private void loadCoinAnimation() {
		Animation anim = new Animation();
		anim.addScene(loadImage("coin1.png"), 150); // pic1
		anim.addScene(loadImage("coin2.png"), 150); // pic2
		anim.addScene(loadImage("coin3.png"), 150); // pic3
		anim.addScene(loadImage("coin2.png"), 150); // pic2
		coinSprite = new Item.Coin(anim);
	}

	public Image loadImage(String name) {
		String filename = "Images/" + name;
		return new ImageIcon(filename).getImage();
	}

	public Image getMirrorImage(Image image) {
		return getTransformedImage(image, -1, 1);
	}

	public Image getFlippedImage(Image image) {
		return getTransformedImage(image, 1, -1);
	}

	private Image getTransformedImage(Image image, float x, float y) {
		AffineTransform transform = new AffineTransform();
		transform.scale(x, y);
		transform.translate((x - 1) * image.getWidth(null) / 2,
				(y - 1) * image.getHeight(null) / 2);

		Image newImage = gc.createCompatibleImage(image.getWidth(null),
				image.getHeight(null), Transparency.BITMASK);

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

	// TODO: Combined with the todo below
	private TileMap loadLevel(String filename) throws IOException {
		ArrayList<String> lines = readFileAndInitiateTileMap(filename);
		addTilesAndSprites(lines);
		addPlayerToMap();
		return currentMap;
	}

	// TODO: Can I extract the width in an easy way so I don't have to initiate
	// the TileMap here? Without having to loop it through it all again..
	private ArrayList<String> readFileAndInitiateTileMap(String filename) throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		int width = 0;

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = reader.readLine();
		do {
			// add every line except for comments (#)
			if (!line.startsWith("#")) {
				lines.add(line);
				width = Math.max(width, line.length());
			}
			line = reader.readLine();
		} while (line != null);
		reader.close();
		currentMap = new TileMap(width, lines.size());
		return lines;
	}

	private void addTilesAndSprites(ArrayList<String> lines) {
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			for (int x = 0; x < line.length(); x++) {
				char ch = line.charAt(x);
				if (ch == 'g') {
					currentMap.setTile(x, y, (Image) tiles.get(0));
				} else if (ch == 'b') {
					addSprite(ammoSprite, x, y);
				} else if (ch == 'c') {
					addSprite(coinSprite, x, y);
				} else if (ch == 'm') {
					addSprite(goalSprite, x, y);
				} else if (ch == '1') {
					addSprite(flyingSprite, x, y);
				} else if (ch == 'e') {
					addSprite(blobSprite, x, y);
				}
			}
		}
	}

	private void addPlayerToMap() {
		Sprite player = (Sprite) playerSprite.clone();
		player.setX(TileMapRenderer.tilesToPixels(DEFAULT_STARTING_X));
		player.setY(DEFAULT_STARTING_Y);
		currentMap.setPlayer(player);
	}

	private void addSprite(Sprite hostSprite, int tileX, int tileY) {
		if (hostSprite != null) {
			Sprite sprite = (Sprite) hostSprite.clone();
			centerSpriteOnTile(sprite, tileX);
			aligntBottomOfSpriteToTile(sprite, tileY);
			currentMap.addSprite(sprite);
		}
	}

	private void centerSpriteOnTile(Sprite sprite, int tileX) {
		sprite.setX(TileMapRenderer.tilesToPixels(tileX)
				+ (TileMapRenderer.tilesToPixels(1) - sprite.getWidth()) / 2);
	}
	
	private void aligntBottomOfSpriteToTile(Sprite sprite, int tileY) {
		sprite.setY(TileMapRenderer.tilesToPixels(tileY + 1)
				- sprite.getHeight());
	}
}
