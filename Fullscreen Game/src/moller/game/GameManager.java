package moller.game;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;

import moller.core.Core;
import moller.core.ResourceManager;
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

	protected TileMap tileMap;
	private Player player;
	private ResourceManager resourceManager;
	private InputManager inputManager;
	private TileMapRenderer tileMapRenderer;
	private UpdateManager updateManager;

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
		tileMap = resourceManager.loadNextLevel();
		updateManager = new UpdateManager(tileMap);
		tileMapRenderer = new TileMapRenderer();
		tileMapRenderer.setTileMap(tileMap);
		tileMapRenderer.setBackground(resourceManager.loadImage("background.png"));
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

	public void draw(Graphics2D g) {
		tileMapRenderer.draw(g);
		drawInfoText(g);
		drawBullets(g);
	}
	
	private void drawInfoText(Graphics2D g) {
		g.drawString("Ammo: " + player.getAmmo(), 2, 20);
		g.drawString("Coins: " + player.getCoins(), screenManager.getWidth() - 100, 20);
	}
	
	// TODO: Change bullets so they included in tileMapRenderer somehow. Change behaviour so it matches creature maybe?
	private void drawBullets(Graphics2D g) {
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
	
	public void update(long elapsedTime) {
		player = (Player) tileMap.getPlayer();
		if (player.getState() == Creature.STATE_DEAD) {
			reloadLevel();
			return;
		}

		checkInput(elapsedTime);
		updateManager.update(elapsedTime, bullets);
	}

	private void reloadLevel() {
		tileMap = resourceManager.reloadLevel();
		tileMapRenderer.setTileMap(tileMap);
		updateManager.setTileMap(tileMap);
	}
	
	// TODO: Find a way to know when the CollisionManager have found player colliding with the Goal Sprite.
	private void loadNextLevel() {
		tileMap = resourceManager.loadNextLevel();
		tileMapRenderer.setTileMap(tileMap);
		updateManager.setTileMap(tileMap);
	}
	
	public void stop() {
		super.stop();
	}
}
