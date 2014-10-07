package moller.sprites;
import java.lang.reflect.Constructor;

public abstract class Creature extends Sprite {

	private static final int DIE_TIME = 1000;

	public static final int STATE_NORMAL = 0;
	public static final int STATE_DYING = 1;
	public static final int STATE_DEAD = 2;

	private Animation left;
	private Animation right;
	private Animation rightDead;
	private Animation leftDead;
	private int state;
	private long stateTime;

	public Creature(Animation right, Animation left, Animation rightDead,
			Animation leftDead) {
		super(right);
		this.left = left;
		this.right = right;
		this.rightDead = rightDead;
		this.leftDead = leftDead;
		state = STATE_NORMAL;
	}

	// Gets the state of the creature
	public int getState() {
		return state;
	}

	public Object clone() {
		// use reflection to create the correct subclass
		Constructor constructor = getClass().getConstructors()[0];
		try {
			return constructor
					.newInstance(new Object[] { (Animation) left.clone(),
							(Animation) right.clone(),
							(Animation) rightDead.clone(),
							(Animation) leftDead.clone() });
		} catch (Exception ex) {
			// should never happen
			ex.printStackTrace();
			return null;
		}
	}

	public float getMaxSpeed() {
		return 0;
	}

	public void setState(int state) {
		if (this.state != state) {
			this.state = state;
			stateTime = 0;
			if (state == STATE_DYING) {
				setVelocityX(0);
				setVelocityY(0);
			}
		}
	}

	/**
	 * Wakes up the creature when the Creature first appears on screen.
	 * Normally, the creature starts moving left.
	 */
	public void wakeUp() {
		if (getState() == STATE_NORMAL && getVelocityX() == 0) {
			setVelocityX(-getMaxSpeed());
		}
	}

	/**
	 * Checks if this creature is alive.
	 */
	public boolean isAlive() {
		return (state == STATE_NORMAL);
	}

	/**
	 * Checks if this creature is flying.
	 */
	public boolean isFlying() {
		return false;
	}

	/**
	 * Called before update() if the creature collided with a tile horizontally.
	 */
	public void collideHorizontal() {
		setVelocityX(-getVelocityX());
	}

	/**
	 * Called before update() if the creature collided with a tile vertically.
	 */
	public void collideVertical() {
		setVelocityY(0);
	}

	/**
	 * Updates the animaton for this creature.
	 */
	public void update(long elapsedTime) {
		// select the correct Animation
		Animation newAnim = a;
		if (getVelocityX() < 0) {
			newAnim = left;
		} else if (getVelocityX() > 0) {
			newAnim = right;
		}
		if (state == STATE_DYING && newAnim == left) {
			newAnim = leftDead;
		} else if (state == STATE_DYING && newAnim == right) {
			newAnim = rightDead;
		}

		// update the Animation
		if (a != newAnim) {
			a = newAnim;
			a.start();
		} else {
			a.update(elapsedTime);
		}

		// update to "dead" state
		stateTime += elapsedTime;
		if (state == STATE_DYING && stateTime >= DIE_TIME) {
			setState(STATE_DEAD);
		}
	}
}
