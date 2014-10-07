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

	private TileMap map;
	private ResourceManager resourceManager;
	private InputManager inputManager;
	private TileMapRenderer renderer;

	private GameAction moveLeft;
	private GameAction moveRight;
	private GameAction jump;
	private GameAction exit;
	private GameAction shoot;

	private GameAction openInventory;

	private GameAction skill_0;
	private GameAction skill_1;
	private GameAction skill_2;
	private GameAction skill_3;
	private GameAction skill_4;
	private GameAction skill_5;
	private GameAction skill_6;
	private GameAction skill_7;
	private GameAction skill_8;
	private GameAction skill_9;

	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();

	public void init() {
		super.init();

		// set up input manager
		initInput();

		// start resource manager
		resourceManager = new ResourceManager(sm.getFullScreenWindow()
				.getGraphicsConfiguration());

		// load resources
		renderer = new TileMapRenderer();
		renderer.setBackground(resourceManager.loadImage("background.png"));

		// load first map
		map = resourceManager.loadNextLevel();
	}

	/**
	 * Closes any resources used by the GameManager.
	 */
	public void stop() {
		super.stop();
	}

	private void initInput() {

		moveLeft = new GameAction("moveLeft");
		moveRight = new GameAction("moveRight");
		jump = new GameAction("jump", GameAction.DETECT_INITAL_PRESS_ONLY);
		exit = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
		shoot = new GameAction("shoot", GameAction.DETECT_INITAL_PRESS_ONLY);

		openInventory = new GameAction("open inventory",
				GameAction.DETECT_INITAL_PRESS_ONLY);

		skill_0 = new GameAction("Skill_0", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill_1 = new GameAction("Skill_1", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill_2 = new GameAction("Skill_2", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill_3 = new GameAction("Skill_3", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill_4 = new GameAction("Skill_4", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill_5 = new GameAction("Skill_5", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill_6 = new GameAction("Skill_6", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill_7 = new GameAction("Skill_7", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill_8 = new GameAction("Skill_8", GameAction.DETECT_INITAL_PRESS_ONLY);
		skill_9 = new GameAction("Skill_9", GameAction.DETECT_INITAL_PRESS_ONLY);

		inputManager = new InputManager(sm.getFullScreenWindow());
		inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

		inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
		inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
		inputManager.mapToKey(jump, KeyEvent.VK_UP);
		inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(shoot, KeyEvent.VK_SPACE);

		inputManager.mapToKey(openInventory, KeyEvent.VK_I);

		// Skillbar
		inputManager.mapToKey(skill_0, KeyEvent.VK_0);
		inputManager.mapToKey(skill_1, KeyEvent.VK_1);
		inputManager.mapToKey(skill_2, KeyEvent.VK_2);
		inputManager.mapToKey(skill_3, KeyEvent.VK_3);
		inputManager.mapToKey(skill_4, KeyEvent.VK_4);
		inputManager.mapToKey(skill_5, KeyEvent.VK_5);
		inputManager.mapToKey(skill_6, KeyEvent.VK_6);
		inputManager.mapToKey(skill_7, KeyEvent.VK_7);
		inputManager.mapToKey(skill_8, KeyEvent.VK_8);
		inputManager.mapToKey(skill_9, KeyEvent.VK_9);

	}

	private void checkInput(long elapsedTime) {

		if (exit.isPressed()) {
			stop();
		}

		Player player = (Player) map.getPlayer();
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
				player.fire();
				fire();
			}
			if (skill_0.isPressed()) {
				player.setWeaponState(0);
			}
			if (skill_1.isPressed()) {
				player.setWeaponState(1);
			}
			if (skill_2.isPressed()) {
				player.setWeaponState(2);
			}
			if (skill_3.isPressed()) {
				player.setWeaponState(3);
			}
			player.setVelocityX(velocityX);
		}

	}

	public void fire() {
		Player player = (Player) map.getPlayer();
		if (player.getAmmo() > 0) {
			Animation anim = new Animation();
			if (player.lookingLeft()) {
				anim.addScene(resourceManager.getMirrorImage(new ImageIcon("Images/bullet1.png").getImage()), 100);
				Bullet b = new Bullet.Normal(anim, player.getX()
						+ player.getWidth(), player.getY());
				b.setVelocityX(-Bullet.BULLET_SPEED);
				bullets.add(b);
			}
			if (!player.lookingLeft()) {
				anim.addScene(new ImageIcon("Images/bullet1.png").getImage(), 100);
				Bullet b = new Bullet.Normal(anim, player.getX(), player.getY());
				b.setVelocityX(Bullet.BULLET_SPEED);
				bullets.add(b);
			}
		}
	}

	public void draw(Graphics2D g) {
		Player player = (Player) map.getPlayer();
		renderer.draw(g, map, sm.getWidth(), sm.getHeight());

		// Draws the text on the screen
		g.drawString("Ammo: " + player.getAmmo(), 2, 20);
		g.drawString("Coins: " + player.getCoins(), sm.getWidth() - 100, 20);

		// Gets the offsetX and offsetY for the bullets.
		int mapWidth = TileMapRenderer.tilesToPixels(map.getWidth());
		int offsetY = sm.getHeight()
				- TileMapRenderer.tilesToPixels(map.getHeight());
		int offsetX = sm.getWidth() / 2 - Math.round(player.getX()) - 64;
		offsetX = Math.min(offsetX, 0);
		offsetX = Math.max(offsetX, sm.getWidth() - mapWidth);

		// Draws the bullets.
		for (int x = 0; x < bullets.size(); x++) {
			Bullet bullet = (Bullet) bullets.get(x);
			if (bullet != null && bullet.isVisible()) {
				g.drawImage(bullet.getImage(), Math.round(bullet.getX())
						+ offsetX, Math.round(bullet.getY()) + offsetY, null);
			}
		}

	}

	/**
	 * Gets the current map.
	 */
	public TileMap getMap() {
		return map;
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
				if (x < 0 || x >= map.getWidth() || map.getTile(x, y) != null) {
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
		Iterator i = map.getSprites();
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
		Creature player = (Creature) map.getPlayer();
		// player is dead! start map over
		if (player.getState() == Creature.STATE_DEAD) {
			map = resourceManager.reloadLevel();
			return;
		}

		// get keyboard/mouse input
		checkInput(elapsedTime);

		// update player
		updateCreature(player, elapsedTime);
		player.update(elapsedTime);

		// update bullets
		if (bullets != null) {
			updateBullets(elapsedTime);
		}

		// update other sprites
		Iterator i = map.getSprites();
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
			sprite.update(elapsedTime);
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
		map.removeSprite(powerUp);
		Player player = (Player) map.getPlayer();

		if (powerUp instanceof Item.Coin) {
			// do something here, like give the player points
			player.setCoins(player.getCoins() + 1);
		} else if (powerUp instanceof Item.Ammo) {
			// Give the player more bullets
			player.setAmmo(Player.WEAPON_PISTOL, 1);
		} else if (powerUp instanceof Item.Goal) {
			// advance to next map
			map = resourceManager.loadNextLevel();
		}
	}
}
