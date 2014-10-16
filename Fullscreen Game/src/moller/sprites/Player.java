package moller.sprites;
import java.util.HashMap;

public class Player extends Creature {

	private static final float JUMP_SPEED = -.95f;

	public static int WEAPON_NO_WEAPON = 0;
	public static int WEAPON_PISTOL = 1;
	public static int WEAPON_MACHINE_GUN = 2;
	public static int WEAPON_ROCKET_LAUNCHER = 3;

	private boolean onGround;
	private int coins = 0;
	private boolean firing;
	private boolean lookingLeft;
	private int weaponUsed;

	private HashMap<Integer, Integer> ammo;

	public Player(Animation right, Animation left, Animation rightDead,
			Animation leftDead) {
		super(right, left, rightDead, leftDead);
		ammo = new HashMap<Integer, Integer>();
		weaponUsed = WEAPON_PISTOL;
		lookingLeft = false;

		ammo.put(0, 0);
		ammo.put(1, 10);
		ammo.put(2, 1);
		ammo.put(3, 1);
	}

	public void collideHorizontal() {
		setVelocityX(0);
	}

	public void collideVertical() {
		if (getVelocityY() > 0) {
			onGround = true;
		}
		setVelocityY(0);
	}

	public void setY(float y) {
		if (Math.round(y) > Math.round(getY())) {
			onGround = false;
		}
		super.setY(y);
	}

	public void wakeUp() {
		// do nothing
	}

	/**
	 * Makes the player jump if the player is on the ground or if forceJump is
	 * true. A forced jump can be double jump for example.
	 */
	public void jump(boolean forceJump) {
		if (onGround || forceJump) {
			onGround = false;
			setVelocityY(JUMP_SPEED);
		}
	}

	public void fire() {
		int weaponUsed = getWeaponUsed();
		if (getAmmo() != 0) {
			ammo.put(weaponUsed, ammo.get(weaponUsed)-1);
		}
	}

	public boolean getFiring() {
		return firing;
	}

	public float getMaxSpeed() {
		return 0.5f;
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public int getAmmo() {
		return ammo.get(getWeaponUsed());
	}

	public int getWeaponUsed() {
		return weaponUsed;
	}

	public void setWeaponUsed(int weaponToUse) {
		this.weaponUsed = weaponToUse;
	}

	public void setAmmo(int bullets, int weapon) {
		ammo.put(weapon, ammo.get(weapon) + bullets);
	}
	
	public boolean lookingLeft() {
		return lookingLeft;
	}
	
	public void lookingLeft(boolean isLookingLeft) {
		lookingLeft = isLookingLeft;
	}
}
