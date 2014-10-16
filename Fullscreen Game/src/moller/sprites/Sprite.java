package moller.sprites;
import java.awt.Image;


public class Sprite {

	protected Animation animation;
	private float x;
	private float y;
	private float velocityX;
	private float velociyY;
	
	public Sprite(Animation a) {
		this.animation = a;
	}
	
	public void updatePosition(long timePassed) {
		x += velocityX* timePassed;
		y += velociyY* timePassed;
		animation.update(timePassed);
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public int getWidth() {
		return animation.getImage().getWidth(null);
	}
	
	public int getHeight() {
		return animation.getImage().getHeight(null);
	}
	
	public float getVelocityX() {
		return velocityX;
	}
	
	public float getVelocityY() {
		return velociyY;
	}
	
	public void setVelocityX(float vx) {
		this.velocityX = vx;
	}
	
	public void setVelocityY(float vy) {
		this.velociyY = vy;
	}
	
	public Image getImage() {
		return animation.getImage();
	}
	
    public Object clone() {
        return new Sprite(animation);
    }
}
