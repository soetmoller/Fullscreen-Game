package moller.sprites;
/********************************************************************************************************************************************************************************************
 * Animation.java
 * 
 * The Animation class carries all kind of animation and makes it possible to add scenes to make the image living.
 * 
 * Made By: Emil Möller
 * Email: emilmol@kth.se
 *******************************************************************************************************************************************************************************************/

import java.awt.Image;
import java.util.ArrayList;

public class Animation {

	private ArrayList scenes;
	private int currentScene;
	private long animationTime;
	private long totalTime;

	public Animation() {
		this(new ArrayList(), 0);
	}
	
    private Animation(ArrayList frames, long totalTime) {
        this.scenes = frames;
        this.totalTime = totalTime;
        start();
    }

	public synchronized void addScene(Image i, long time) {
		totalTime += time;
		scenes.add(new OneScene(i, totalTime));
	}

	public synchronized void start() {
		animationTime = 0;
		currentScene = 0;
	}
	
    public Object clone() {
        return new Animation(scenes, totalTime);
    }

	public synchronized void update(long timePassed) {
		if (scenes.size() > 1) {
			animationTime += timePassed;

			if (animationTime >= totalTime) {
				animationTime = 0;
				currentScene = 0;
			}
			while (animationTime > getScene(currentScene).endTime) {
				currentScene++;
			}
		}
	}

	public synchronized Image getImage() {
		if (scenes.size() == 0) {
			return null;
		} else {
			return getScene(currentScene).pic;
		}
	}

	private OneScene getScene(int x) {
		return (OneScene) scenes.get(x);
	}
	
	private class OneScene {
		
		Image pic;
		long endTime;

		public OneScene(Image pic, long endTime) {
			this.pic = pic;
			this.endTime = endTime;
		}
	}
}
