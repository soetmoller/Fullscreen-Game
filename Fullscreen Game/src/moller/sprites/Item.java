package moller.sprites;
import java.lang.reflect.Constructor;

public abstract class Item extends Sprite {

	public Item(Animation a) {
		super(a);
	}


    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(
                new Object[] {(Animation)a.clone()});
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
	
	// Collect Bullets to kill enemies
	public static class Ammo extends Item {
		public Ammo(Animation anim) {
			super(anim);
		}
	}

	// Collect coins
	public static class Coin extends Item {
		public Coin(Animation anim) {
			super(anim);
		}
	}

	// When the player reaches the goal he wins.
	public static class Goal extends Item {
		public Goal(Animation anim) {
			super(anim);
		}
	}
}
