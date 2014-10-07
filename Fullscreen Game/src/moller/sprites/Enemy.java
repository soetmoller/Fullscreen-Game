package moller.sprites;

public abstract class Enemy extends Creature{
	
	public Enemy(Animation right, Animation left, Animation rightDead, Animation leftDead) {
		super(left, right, rightDead, leftDead);
	}
	
	public float getMaxSpeed() {
		return 0.5f;
	}
	
	public static class Blob extends Enemy {
		public Blob(Animation right, Animation left, Animation rightDead, Animation leftDead) {
			super(right, left, rightDead, leftDead);
		}
	}

}
