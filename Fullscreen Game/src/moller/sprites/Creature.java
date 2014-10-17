package moller.sprites;
import java.lang.reflect.Constructor;

public abstract class Creature extends Sprite {

	private static final int TIME_TO_DIE = 1000;

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

	public int getState() {
		return state;
	}

	public Object clone() {
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

	public void wakeUp() {
		if (getState() == STATE_NORMAL && getVelocityX() == 0) {
			setVelocityX(-getMaxSpeed());
		}
	}

	public boolean isAlive() {
		return (state == STATE_NORMAL);
	}

	public boolean isFlying() {
		return false;
	}

	public void collideHorizontal() {
		setVelocityX(-getVelocityX());
	}

	public void collideVertical() {
		setVelocityY(0);
	}

	public void update(long elapsedTime) {
		Animation newAnimation = getCorrectAnimation();

		if (animation != newAnimation) {
			animation = newAnimation;
			animation.start();
		} else {
			animation.update(elapsedTime);
		}

		stateTime += elapsedTime;
		if (state == STATE_DYING && stateTime >= TIME_TO_DIE) {
			setState(STATE_DEAD);
		}
	}
	
	private Animation getCorrectAnimation() {
		Animation newAnimation = animation;
		if (getVelocityX() < 0) {
			newAnimation = left;
		} else if (getVelocityX() > 0) {
			newAnimation = right;
		}
		if (state == STATE_DYING && newAnimation == left) {
			newAnimation = leftDead;
		} else if (state == STATE_DYING && newAnimation == right) {
			newAnimation = rightDead;
		}
		return newAnimation;
	}
}
