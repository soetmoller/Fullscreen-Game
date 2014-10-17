package moller.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import moller.resource.TileMap;
import moller.resource.TileMapRenderer;
import moller.sprites.Bullet;
import moller.sprites.Creature;
import moller.sprites.Player;
import moller.sprites.Sprite;

public class UpdateManager {
	

	public static final float GRAVITY = 0.002f;

	private TileMap tileMap;
	private CollisionManager collisionManager;

	public UpdateManager(TileMap tileMap) {
		this.tileMap = tileMap;
		collisionManager = new CollisionManager(tileMap);
	}

	public TileMap getTileMap() {
		return tileMap;
	}

	public void setTileMap(TileMap tileMap) {
		this.tileMap = tileMap;
		collisionManager.setTileMap(tileMap);
	}

	public void update(long elapsedTime, ArrayList<Bullet> bullets) {
		updatePlayer(elapsedTime);
		updateBullets(elapsedTime, bullets);
		updateSprites(elapsedTime);
	}
	
	private void updatePlayer(long elapsedTime) {
		Player player = (Player) tileMap.getPlayer();
		updateCreature(player, elapsedTime);
		player.update(elapsedTime);
	}

	private void updateBullets(long elapsedTime, ArrayList<Bullet> bullets) {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet bullet = (Bullet) bullets.get(i);

			changeSpritePosition(bullet, elapsedTime);
			boolean canKill = (bullet.isVisible());
			collisionManager.checkBulletCollision(bullet, canKill);
		}
	}

	private void updateSprites(long elapsedTime) {
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
			sprite.update(elapsedTime);
		}
	}

	private void updateCreature(Creature creature, long elapsedTime) {
		if (!creature.isFlying()) {
			creature.setVelocityY(creature.getVelocityY() + GRAVITY
					* elapsedTime);
		}
		float oldY = creature.getY();
		changeSpritePosition(creature, elapsedTime);
		if (creature instanceof Player) {
			boolean canKill = (oldY < creature.getY());
			collisionManager.checkPlayerCollision((Player) creature, canKill);
		}
	}
	
	private void changeSpritePosition(Sprite sprite, long elapsedTime) {
		changeSpriteXPosition(sprite, elapsedTime);
		changeSpriteYPosition(sprite, elapsedTime);
	}
	
	private void changeSpriteXPosition(Sprite sprite, long elapsedTime) {
		float dx = sprite.getVelocityX();
		float oldX = sprite.getX();
		float newX = oldX + dx * elapsedTime;
		Point tile = collisionManager.getTileCollision(sprite, newX, sprite.getY());
		if (tile == null) {
			sprite.setX(newX);
		} else {
			if (dx > 0) {
				sprite.setX(TileMapRenderer.tilesToPixels(tile.x)
						- sprite.getWidth());
			} else if (dx < 0) {
				sprite.setX(TileMapRenderer.tilesToPixels(tile.x + 1));
			}
			if(sprite instanceof Creature)
				((Creature)sprite).collideHorizontal();
			if(sprite instanceof Bullet)
				((Bullet)sprite).setVisible(false);
		}
	}
	
	private void changeSpriteYPosition(Sprite sprite, long elapsedTime) {
		float dy = sprite.getVelocityY();
		float oldY = sprite.getY();
		float newY = oldY + dy * elapsedTime;
		Point tile = collisionManager.getTileCollision(sprite, sprite.getX(), newY);
		if (tile == null) {
			sprite.setY(newY);
		} else {
			// line up with the tile boundary
			if (dy > 0) {
				sprite.setY(TileMapRenderer.tilesToPixels(tile.y)
						- sprite.getHeight());
			} else if (dy < 0) {
				sprite.setY(TileMapRenderer.tilesToPixels(tile.y + 1));
			}
			if(sprite instanceof Creature)
				((Creature)sprite).collideVertical();
			if(sprite instanceof Bullet)
				((Bullet)sprite).setVisible(false);
		}
	}
}
