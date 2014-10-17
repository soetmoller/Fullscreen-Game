package moller.game;

import java.awt.Point;
import java.util.Iterator;

import moller.resource.TileMap;
import moller.resource.TileMapRenderer;
import moller.sprites.Bullet;
import moller.sprites.Creature;
import moller.sprites.Item;
import moller.sprites.Player;
import moller.sprites.Sprite;

public class CollisionManager {
	
	private TileMap tileMap;
	
	public CollisionManager(TileMap tileMap) {
		this.tileMap = tileMap;
	}
	
	protected void setTileMap(TileMap tileMap) {
		this.tileMap = tileMap;
	}
	
	public void checkBulletCollision(Bullet bullet, boolean canKill) {
		if (!bullet.isVisible()) {
			return;
		}
		Sprite collisionSprite = getSpriteCollision(bullet);
		if (collisionSprite instanceof Creature) {
			Creature enemyCreature = (Creature) collisionSprite;
			if (canKill) {
				enemyCreature.setState(Creature.STATE_DYING);
				bullet.setY(enemyCreature.getY() - bullet.getHeight());
				bullet.setVisible(false);
			} else {
				bullet.setVisible(false);
			}
		}
	}
	
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

		return checkTilesForCollision(fromTileX, toTileX, fromTileY, toTileY);
	}
	
	private Point checkTilesForCollision(int fromTileX, int toTileX, int fromTileY, int toTileY) {
		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				if (x < 0 || x >= tileMap.getWidth() || tileMap.getTile(x, y) != null) {
					return new Point(x, y);
				}
			}
		}
		return null;
	}

	public void checkPlayerCollision(Player player, boolean canKill) {
		if (!player.isAlive()) {
			return;
		}

		Sprite collisionSprite = getSpriteCollision(player);
		if (collisionSprite instanceof Item) {
			acquirePowerUp(player, (Item) collisionSprite);
		} else if (collisionSprite instanceof Creature) {
			Creature enemyCreature = (Creature) collisionSprite;
			if (canKill) {
				enemyCreature.setState(Creature.STATE_DYING);
				player.setY(enemyCreature.getY() - player.getHeight());
				player.jump(true);
			} else {
				player.setState(Creature.STATE_DYING);
			}
		}
	}
	
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

	public boolean isCollision(Sprite sprite1, Sprite sprite2) {
		if (sprite1 == sprite2)
			return false;
		return  isSpritesAlive(sprite1, sprite2) && spritesInteresects(sprite1, sprite2);
	}
	
	private boolean isSpritesAlive(Sprite sprite1, Sprite sprite2) {
		if (sprite1 instanceof Creature && !((Creature) sprite1).isAlive()) {
			return false;
		}
		if (sprite2 instanceof Creature && !((Creature) sprite2).isAlive()) {
			return false;
		}
		return true;
	}
	
	private boolean spritesInteresects(Sprite sprite1, Sprite sprite2) {
		int s1x = Math.round(sprite1.getX());
		int s1y = Math.round(sprite1.getY());
		int s2x = Math.round(sprite2.getX());
		int s2y = Math.round(sprite2.getY());

		return (s1x < s2x + sprite2.getWidth() && s2x < s1x + sprite1.getWidth()
				&& s1y < s2y + sprite2.getHeight() && s2y < s1y + sprite1.getHeight());
	}
	
	public void acquirePowerUp(Player player, Item powerUp) {
		if (powerUp instanceof Item.Coin) {
			player.setCoins(player.getCoins() + 1);
		} else if (powerUp instanceof Item.Ammo) {
			player.setAmmo(Player.WEAPON_PISTOL, 1);
		} else if (powerUp instanceof Item.Goal) {
			// advance to next map
//TODO			tileMap = resourceManager.loadNextLevel();
		}
		tileMap.removeSprite(powerUp);
	}
}
