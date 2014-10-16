package moller.sprites;
import java.lang.reflect.Constructor;

public abstract class Item extends Sprite {

	public Item(Animation animation) {
		super(animation);
	}

    public Object clone() {
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(
                new Object[] {(Animation)animation.clone()});
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
	
	public static class Ammo extends Item {
		public Ammo(Animation animation) {
			super(animation);
		}
	}

	public static class Coin extends Item {
		public Coin(Animation animation) {
			super(animation);
		}
	}

	public static class Goal extends Item {
		public Goal(Animation animation) {
			super(animation);
		}
	}
}
