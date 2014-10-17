package moller.core;
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
	protected ScreenManager screenManager;

	public void stop() {
		running = false;
	}

	public void run() {
		try {
			init();
			gameLoop();
		} finally {
			screenManager.restoreScreen();
		}
	}

	public void init() {
		screenManager = new ScreenManager();
		DisplayMode dm = screenManager.findFirstCompatibleDisplayMode(modes);
		screenManager.setFullScreen(dm);

		Window window = screenManager.getFullScreenWindow();
		window.setFont(new Font("Arial", Font.PLAIN, 24));
		window.setBackground(Color.GREEN);
		window.setForeground(Color.WHITE);
		running = true;
	}

	public Image loadImage(String fileName) {
		return new ImageIcon(fileName).getImage();
	}

	public void gameLoop() {
		long startingTime = System.currentTimeMillis();
		long cumulativeTime = startingTime;

		while (running) {
			long timePassed = System.currentTimeMillis() - cumulativeTime;
			cumulativeTime += timePassed;

			update(timePassed);

			Graphics2D g = screenManager.getGraphics();
			draw(g);
			g.dispose();
			screenManager.update();

			try {
				Thread.sleep(20);
			} catch (Exception exception) {
				exception.toString();
			}
		}
	}

	public abstract void update(long timePassed);
	public abstract void draw(Graphics2D g);
}
