package moller.sprites;
public abstract class Bullet extends Sprite {

	public static final float BULLET_SPEED = 0.75f;
	private boolean isVisible;
	
	public Bullet(Animation a, float initialX, float initialY) {
		super(a);
		this.setX(initialX);
		this.setY(initialY);
		isVisible = true;
	}
	
	public void collideHorizontal() {
		setVisible(false);
	}
	
	public void collideVertical() {
		setVisible(false);
	}
	
	public void setVisible(boolean var) {
		isVisible = var;
	}
	
	public boolean isVisible() {
		return isVisible;
	}

	public static class HandGunBullet extends Bullet {

		public HandGunBullet(Animation a, float initialX, float initialY) {
			super(a, initialX, initialY);
		}
	}
}
