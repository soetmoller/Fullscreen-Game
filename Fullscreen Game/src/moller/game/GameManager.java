package moller.game;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;

import moller.input.GameAction;
import moller.input.InputManager;
import moller.resource.TileMap;
import moller.resource.TileMapRenderer;
import moller.sprites.Animation;
import moller.sprites.Bullet;
import moller.sprites.Creature;
import moller.sprites.Item;
import moller.sprites.Player;
import moller.sprites.Sprite;

/**
 * GameManager manages all parts of the game.
 */
public class GameManager extends Core {

	public static void main(String[] args) {
		new GameManager().run();
	}

	public static final float GRAVITY = 0.002f;

	private TileMap tileMap;
	private Player player;
	private ResourceManager resourceManager;
	private InputManager inputManager;
	private TileMapRenderer tileMapRenderer;

	private GameAction moveLeft;
	private GameAction moveRight;
	private GameAction jump;
	private GameAction exit;
	private GameAction shoot;

	private GameAction skill0;
	private GameAction skill1;

	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();

	public void init() {
		super.init();
		initGameActions();
		initInput();
		resourceManager = new ResourceManager(screenManager.getFullScreenWindow()
				.getGraphicsConfiguration());
		tileMapRenderer = new TileMapRenderer();
		tileMapRenderer.setBackground(resourceManager.loadImage("background.png"));
		tileMap = resourceManager.loadNextLevel();
		tileMapRenderer.setTileMap(tileMap);
		tileMapRenderer.setScreenHeight(screenManager.getHeight());
		tileMapRenderer.setScreenWidth(screenManager.getWidth());
	}
	
	private void initGameActions() {
		moveLeft = new GameAction("moveLeft");
		moveRight = new GameAction("moveRight");
		jump = new GameAction("jump", GameAction.DETECT_INITAL_PRESS_ONLY);
		exit = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
		shoot = new GameAction("shoot", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill0 = new GameAction("Skill_0", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill1 = new GameAction("Skill_1", GameAction.DETECT_INITAL_PRESS_ONLY);
	}

	private void initInput() {
		inputManager = new InputManager(screenManager.getFullScreenWindow());
		inputManager.setCursor(InputManager.INVISIBLE_CURSOR);
		inputManager.mapActionToKey(moveLeft, KeyEvent.VK_LEFT);
		inputManager.mapActionToKey(moveRight, KeyEvent.VK_RIGHT);
		inputManager.mapActionToKey(jump, KeyEvent.VK_UP);
		inputManager.mapActionToKey(exit, KeyEvent.VK_ESCAPE);
		inputManager.mapActionToKey(shoot, KeyEvent.VK_SPACE);
		inputManager.mapActionToKey(skill0, KeyEvent.VK_0);
		inputManager.mapActionToKey(skill1, KeyEvent.VK_1);

	}

	private void checkInput(long elapsedTime) {
		if (exit.isPressed()) {
			stop();
		}
		if (player.isAlive()) {
			float velocityX = 0;
			if (moveLeft.isPressed()) {
				velocityX -= player.getMaxSpeed();
				player.lookingLeft(true);
			}
			if (moveRight.isPressed()) {
				velocityX += player.getMaxSpeed();
				player.lookingLeft(false);
			}
			if (jump.isPressed()) {
				player.jump(false);
			}
			if (shoot.isPressed()) {
				fire();
			}
			if (skill0.isPressed()) {
				player.setWeaponUsed(0);
			}
			if (skill1.isPressed()) {
				player.setWeaponUsed(1);
			}
			player.setVelocityX(velocityX);
		}

	}

	public void fire() {
		if (player.getAmmo() > 0) {
			player.fire();
			bullets.add(createBullet());
		}
	}
	
	private Bullet createBullet() {
		Animation animation = new Animation();
		float velocityX;
		if(player.lookingLeft()) {
			animation.addScene(resourceManager.getMirrorImage(new ImageIcon("Images/bullet1.png").getImage()), 100);
			velocityX = -Bullet.BULLET_SPEED;
		} else {
			animation.addScene(new ImageIcon("Images/bullet1.png").getImage(), 100);
			velocityX = Bullet.BULLET_SPEED;
		}
		Bullet bullet = new Bullet.HandGunBullet(animation, player.getX(), player.getY());
		bullet.setVelocityX(velocityX);
		return bullet;
	}

	public void draw(Graphics2D g) {
		tileMapRenderer.draw(g);
		drawInfoText(g);

		// Gets the offsetX and offsetY for the bullets.
		int mapWidth = TileMapRenderer.tilesToPixels(tileMap.getWidth());
		int offsetY = screenManager.getHeight()
				- TileMapRenderer.tilesToPixels(tileMap.getHeight());
		int offsetX = screenManager.getWidth() / 2 - Math.round(player.getX()) - 64;
		offsetX = Math.min(offsetX, 0);
		offsetX = Math.max(offsetX, screenManager.getWidth() - mapWidth);

		// Draws the bullets.
		for (int x = 0; x < bullets.size(); x++) {
			Bullet bullet = (Bullet) bullets.get(x);
			if (bullet != null && bullet.isVisible()) {
				g.drawImage(bullet.getImage(), Math.round(bullet.getX())
						+ offsetX, Math.round(bullet.getY()) + offsetY, null);
			}
		}

	}
	
	private void drawInfoText(Graphics2D g) {
		g.drawString("Ammo: " + player.getAmmo(), 2, 20);
		g.drawString("Coins: " + player.getCoins(), screenManager.getWidth() - 100, 20);
	}

	/**
	 * Gets the tile that a Sprites collides with. Only the Sprite's X or Y
	 * should be changed, not both. Returns null if no collision is detected.
	 */
	public Point getTileCollision(Sprite sprite, float newX, float newY) {
		float fromX = Math.min(sprite.getX(), newX);
		float fromY = Math.min(sprite.getY(), newY);
		float toX = Math.max(sprite.getX(), newX);
		float toY = Math.max(sprite.getY(), newY);

		// get the tile locations
		int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
		int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
		int toTileX = TileMapRenderer
				.pixelsToTiles(toX + sprite.getWidth() - 1);
		int toTileY = TileMapRenderer.pixelsToTiles(toY + sprite.getHeight()
				- 1);

		// check each tile for a collision
		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				if (x < 0 || x >= tileMap.getWidth() || tileMap.getTile(x, y) != null) {
					// collision found, return the tile
					Point pointCache = new Point(x, y);
					return pointCache;
				}
			}
		}
		// no collision found
		return null;
	}

	/**
	 * Checks if two Sprites collide with one another. Returns false if the two
	 * Sprites are the same. Returns false if one of the Sprites is a Creature
	 * that is not alive.
	 */
	public boolean isCollision(Sprite s1, Sprite s2) {
		// if the Sprites are the same, return false
		if (s1 == s2) {
			return false;
		}

		// if one of the Sprites is a dead Creature, return false
		if (s1 instanceof Creature && !((Creature) s1).isAlive()) {
			return false;
		}
		if (s2 instanceof Creature && !((Creature) s2).isAlive()) {
			return false;
		}

		// get the pixel location of the Sprites
		int s1x = Math.round(s1.getX());
		int s1y = Math.round(s1.getY());
		int s2x = Math.round(s2.getX());
		int s2y = Math.round(s2.getY());

		// check if the two sprites' boundaries intersect
		return (s1x < s2x + s2.getWidth() && s2x < s1x + s1.getWidth()
				&& s1y < s2y + s2.getHeight() && s2y < s1y + s1.getHeight());
	}

	/**
	 * Gets the Sprite that collides with the specified Sprite, or null if no
	 * Sprite collides with the specified Sprite.
	 */
	public Sprite getSpriteCollision(Sprite sprite) {
		Iterator i = tileMap.getSprites();
		while (i.hasNext()) {
			Sprite otherSprite = (Sprite) i.next();
			if (isCollision(sprite, otherSprite)) {
				return otherSprite;
			}
		}
		return null;
	}

	/**
	 * Updates Animation, position, and velocity of all Sprites in the current
	 * map.
	 */
	public void update(long elapsedTime) {
		player = (Player) tileMap.getPlayer();
		// player is dead! start map over
		if (player.getState() == Creature.STATE_DEAD) {
			tileMap = resourceManager.reloadLevel();
			return;
		}

		// get keyboard/mouse input
		checkInput(elapsedTime);

		// update player
		updateCreature(player, elapsedTime);
		player.updatePosition(elapsedTime);

		// update bullets
		if (bullets != null) {
			updateBullets(elapsedTime);
		}

		// update other sprites
		Iterator i = tileMap.getSprites();
		while (i.hasNext()) {
			Sprite sprite = (Sprite) i.next();
			if (sprite instanceof Creature) {
				Creature creature = (Creature) sprite;
				if (creature.getState() == Creature.STATE_DEAD) {
					i.remove();
				} else {
					updateCreature(creature, elapsedTime);
				}
			}
			sprite.updatePosition(elapsedTime);
		}
	}

	/**
	 * Updates the creature, applying gravity for creatures that aren't flying,
	 * and checks collisions.
	 */
	private void updateCreature(Creature creature, long elapsedTime) {

		// apply gravity
		if (!creature.isFlying()) {
			creature.setVelocityY(creature.getVelocityY() + GRAVITY
					* elapsedTime);
		}

		// change x
		float dx = creature.getVelocityX();
		float oldX = creature.getX();
		float newX = oldX + dx * elapsedTime;
		Point tile = getTileCollision(creature, newX, creature.getY());
		if (tile == null) {
			creature.setX(newX);
		} else {
			// line up with the tile boundary

			if (dx > 0) {
				creature.setX(TileMapRenderer.tilesToPixels(tile.x)
						- creature.getWidth());
			} else if (dx < 0) {
				creature.setX(TileMapRenderer.tilesToPixels(tile.x + 1));
			}
			creature.collideHorizontal();
		}
		if (creature instanceof Player) {
			checkPlayerCollision((Player) creature, false);
		}

		// change y
		float dy = creature.getVelocityY();
		float oldY = creature.getY();
		float newY = oldY + dy * elapsedTime;
		tile = getTileCollision(creature, creature.getX(), newY);
		if (tile == null) {
			creature.setY(newY);
		} else {
			// line up with the tile boundary
			if (dy > 0) {
				creature.setY(TileMapRenderer.tilesToPixels(tile.y)
						- creature.getHeight());
			} else if (dy < 0) {
				creature.setY(TileMapRenderer.tilesToPixels(tile.y + 1));
			}
			creature.collideVertical();
		}
		if (creature instanceof Player) {
			boolean canKill = (oldY < creature.getY());
			checkPlayerCollision((Player) creature, canKill);
		}
	}

	/**
	 * Checks for Player collision with other Sprites. If canKill is true,
	 * collisions with Creatures will kill them.
	 */
	public void checkPlayerCollision(Player player, boolean canKill) {
		if (!player.isAlive()) {
			return;
		}

		// check for player collision with other sprites
		Sprite collisionSprite = getSpriteCollision(player);
		if (collisionSprite instanceof Item) {
			acquirePowerUp((Item) collisionSprite);
		} else if (collisionSprite instanceof Creature) {
			Creature badguy = (Creature) collisionSprite;
			if (canKill) {
				// kill the badguy and make player bounce
				badguy.setState(Creature.STATE_DYING);
				player.setY(badguy.getY() - player.getHeight());
				player.jump(true);
			} else {
				// player dies!
				player.setState(Creature.STATE_DYING);
			}
		}
	}

	// These next two methods will repeat same thing as in the last two methods
	public void updateBullets(long elapsedTime) {
		// change x
		for (int x = 0; x < bullets.size(); x++) {
			Bullet bullet = (Bullet) bullets.get(x);

			float dx = bullet.getVelocityX();
			float oldX = bullet.getX();
			float newX = oldX + dx * elapsedTime;
			Point tile = getTileCollision(bullet, newX, bullet.getY());
			if (tile == null) {
				bullet.setX(newX);
			} else {
				if (dx > 0) {
					bullet.setX(TileMapRenderer.tilesToPixels(tile.x)
							- bullet.getWidth());
				} else if (dx < 0) {
					bullet.setX(TileMapRenderer.tilesToPixels(tile.x + 1));
				}
				bullet.collideHorizontal();
			}

			// change y
			float dy = bullet.getVelocityY();
			float oldY = bullet.getY();
			float newY = oldY + dy * elapsedTime;
			tile = getTileCollision(bullet, bullet.getX(), newY);
			if (tile == null) {
				bullet.setY(newY);
			} else {
				// line up with the tile boundary
				if (dy > 0) {
					bullet.setY(TileMapRenderer.tilesToPixels(tile.y)
							- bullet.getHeight());
				} else if (dy < 0) {
					bullet.setY(TileMapRenderer.tilesToPixels(tile.y + 1));
				}
				bullet.collideVertical();
			}
			boolean canKill = (bullet.isVisible());
			checkBulletCollision(bullet, canKill);
		}
	}

	public void checkBulletCollision(Bullet bullet, boolean canKill) {
		if (!bullet.isVisible()) {
			return;
		}

		// check for player collision with other sprites
		Sprite collisionSprite = getSpriteCollision(bullet);
		if (collisionSprite instanceof Creature) {
			Creature badguy = (Creature) collisionSprite;
			if (canKill) {
				// kill the badguy and make bullet invisible
				badguy.setState(Creature.STATE_DYING);
				bullet.setY(badguy.getY() - bullet.getHeight());
				bullet.setVisible(false);
			} else {
				// makes it invisible
				bullet.setVisible(false);
			}
		}
	}

	/**
	 * Gives the player the speicifed power up and removes it from the map.
	 */
	public void acquirePowerUp(Item powerUp) {
		// remove it from the map
		tileMap.removeSprite(powerUp);

		if (powerUp instanceof Item.Coin) {
			// do something here, like give the player points
			player.setCoins(player.getCoins() + 1);
		} else if (powerUp instanceof Item.Ammo) {
			// Give the player more bullets
			player.setAmmo(Player.WEAPON_PISTOL, 1);
		} else if (powerUp instanceof Item.Goal) {
			// advance to next map
			tileMap = resourceManager.loadNextLevel();
		}
	}

	public void stop() {
		super.stop();
	}
}
