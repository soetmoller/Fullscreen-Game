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
	private int sceneIndex;
	private long movieTime;
	private long totalTime;

	public Animation() {
		this(new ArrayList(), 0);
	}
	
    private Animation(ArrayList frames, long totalTime) {
        this.scenes = frames;
        this.totalTime = totalTime;
        start();
    }

	// add scene to ArrayList and set time for each scene
	public synchronized void addScene(Image i, long time) {
		totalTime += time;
		scenes.add(new OneScene(i, totalTime));
	}

	public synchronized void start() {
		movieTime = 0;
		sceneIndex = 0;
	}
	
    public Object clone() {
        return new Animation(scenes, totalTime);
    }

	public synchronized void update(long timePassed) {
		if (scenes.size() > 1) {
			movieTime += timePassed;

			// restart
			if (movieTime >= totalTime) {
				movieTime = 0;
				sceneIndex = 0;
			}
			while (movieTime > getScene(sceneIndex).endTime) {
				sceneIndex++;
			}
		}
	}

	public synchronized Image getImage() {
		if (scenes.size() == 0) {
			return null;
		} else {
			return getScene(sceneIndex).pic;
		}
	}

	// get scene
	private OneScene getScene(int x) {
		return (OneScene) scenes.get(x);
	}

	////////// PRIVATE INNER CLASS ///////////////////
	private class OneScene {

		Image pic;
		long endTime;

		public OneScene(Image pic, long endTime) {

			this.pic = pic;
			this.endTime = endTime;
		}
	}
}
