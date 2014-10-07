package moller.game;
/********************************************************************************************************************************************************************************************
 * Core.java
 * 
 * The Core class uses all the important classes and making it the core structure of the game.
 * 
 * Made By: Emil Möller
 * Email: emilmol@kth.se
 *******************************************************************************************************************************************************************************************/

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Window;

import javax.swing.ImageIcon;

public abstract class Core {

	private static DisplayMode modes[] = { new DisplayMode(800, 600, 32, 0),
			new DisplayMode(800, 600, 24, 0), new DisplayMode(800, 600, 16, 0),
			new DisplayMode(640, 480, 32, 0), new DisplayMode(640, 480, 24, 0),
			new DisplayMode(640, 480, 16, 0) };

	private boolean running;
	protected ScreenManager sm;

	public void stop() {
		running = false;
	}

	// call init and gameloop
	public void run() {
		try {
			init();
			gameLoop();
		} finally {
			sm.restoreScreen();
		}
	}

	// set full screen
	public void init() {
		sm = new ScreenManager();
		DisplayMode dm = sm.findFirstCompatibleDisplayMode(modes);
		sm.setFullScreen(dm);

		Window w = sm.getFullScreenWindow();
		w.setFont(new Font("Arial", Font.PLAIN, 24));
		w.setBackground(Color.GREEN);
		w.setForeground(Color.WHITE);
		running = true;
	}

	public Image loadImage(String fileName) {
		return new ImageIcon(fileName).getImage();
	}

	// main gameLoop
	public void gameLoop() {
		long startingTime = System.currentTimeMillis();
		long cumTime = startingTime;

		while (running) {
			long timePassed = System.currentTimeMillis() - cumTime;
			cumTime += timePassed;

			update(timePassed);

			Graphics2D g = sm.getGraphics();
			draw(g);
			g.dispose();
			sm.update();

			try {
				Thread.sleep(20);
			} catch (Exception exception) {
				exception.toString();
			}
		}
	}

	// update method for use in child class
	public abstract void update(long timePassed);

	// draws to the screen in child class
	public abstract void draw(Graphics2D g);
}
