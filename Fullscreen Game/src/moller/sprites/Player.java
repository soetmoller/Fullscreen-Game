package moller.sprites;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;

public class Player extends Creature {

	private static final float JUMP_SPEED = -.95f;

	private boolean onGround;
	private int coins = 0;
	private boolean firing;
	private boolean lookingLeft;
	private int weaponState;
	public static int WEAPON_NO = 0;
	public static int WEAPON_PISTOL = 1;
	public static int WEAPON_MACHINE_GUN = 2;
	public static int WEAPON_ROCKET_LAUNCHER = 3;

	private List<Integer> pistolAmmo = new ArrayList<Integer>();
	private List<Integer> machineGunAmmo = new ArrayList<Integer>();
	private List<Integer> rocketLauncerAmmo = new ArrayList<Integer>();

	private HashMap<Integer, List<Integer>> ammo;

	public Player(Animation right, Animation left, Animation rightDead,
			Animation leftDead) {
		super(right, left, rightDead, leftDead);
		ammo = new HashMap<Integer, List<Integer>>(3);
		weaponState = WEAPON_PISTOL;
		lookingLeft = false;

		for (int x = 0; x < 10; x++) {
			pistolAmmo.add(x);
		}
		ammo.put(0, new ArrayList<Integer>());
		ammo.put(1, pistolAmmo);
		ammo.put(2, machineGunAmmo);
		ammo.put(3, rocketLauncerAmmo);
	}

	public void collideHorizontal() {
		setVelocityX(0);
	}

	public void collideVertical() {
		// check if collided with ground
		if (getVelocityY() > 0) {
			onGround = true;
		}
		setVelocityY(0);
	}

	public void setY(float y) {
		// check if falling
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
	 * true.
	 */
	public void jump(boolean forceJump) {
		if (onGround || forceJump) {
			onGround = false;
			setVelocityY(JUMP_SPEED);
		}
	}

	public void fire() {
		int state = getWeaponState();
		if (getAmmo() != 0) {
			if (state == 1) {
				pistolAmmo.remove(0);
			} else if (state == 2) {
				machineGunAmmo.remove(0);
			} else if (state == 3) {
				rocketLauncerAmmo.remove(0);
			} else
				System.out.println("WRONG");
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
		return ammo.get(getWeaponState()).size();
	}

	public int getWeaponState() {
		return weaponState;
	}

	public void setWeaponState(int WEAPON_STATE) {
		this.weaponState = WEAPON_STATE;
	}

	public void setAmmo(int bullets, int WEAPON_STATE) {
		if (WEAPON_STATE == 1) {
			for (int x = 0; x < bullets; x++) {
				pistolAmmo.add(1);
			}
		} else if (WEAPON_STATE == 2) {
			for (int x = 0; x < bullets; x++) {
				machineGunAmmo.add(1);
			}
		} else if (WEAPON_STATE == 3) {
			for (int x = 0; x < bullets; x++) {
				rocketLauncerAmmo.add(1);
			}
		} else
			System.out.println("WRONG");
	}
	
	public boolean lookingLeft() {
		return lookingLeft;
	}
	
	public void lookingLeft(boolean var) {
		lookingLeft = var;
	}
}
