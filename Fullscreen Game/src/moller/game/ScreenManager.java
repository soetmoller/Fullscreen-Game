package moller.game;
/********************************************************************************************************************************************************************************************
 * ScreenManager.java
 * 
 * The ScreenManager class finds the best graphical environment for the used computer and can manage the application to fullscreen
 * 
 * Made By: Emil Möller
 * Email: emilmol@kth.se
 *******************************************************************************************************************************************************************************************/

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class ScreenManager {

	private GraphicsDevice graphicsDevice;

	public ScreenManager() {
		GraphicsEnvironment environment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		graphicsDevice = environment.getDefaultScreenDevice();
	}

	public DisplayMode[] getCompatibleDisplayModes() {
		return graphicsDevice.getDisplayModes();
	}

	public DisplayMode findFirstCompatibleDisplayMode(DisplayMode modes[]) {
		DisplayMode goodModes[] = graphicsDevice.getDisplayModes();
		for (int i = 0; i < modes.length; i++) {
			for (int x = 0; x < goodModes.length; x++) {
				if (displayModesMatch(modes[i], goodModes[x])) {
					return modes[i];
				}
			}
		}
		return null;
	}

	public DisplayMode getCurrentDisplayMode() {
		return graphicsDevice.getDisplayMode();
	}

	public boolean displayModesMatch(DisplayMode displayMode1, DisplayMode displayMode2) {
		if (displayMode1.getWidth() != displayMode2.getWidth() || displayMode1.getHeight() != displayMode2.getHeight()) {
			return false;
		}
		if (displayMode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
				&& displayMode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
				&& displayMode1.getBitDepth() != displayMode2.getBitDepth()) {
			return false;
		}
		if (displayMode1.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
				&& displayMode2.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
				&& displayMode1.getRefreshRate() != displayMode2.getRefreshRate()) {
			return false;
		}

		return true;
	}

	public void setFullScreen(DisplayMode displayMode) {
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		frame.setIgnoreRepaint(true);
		frame.setResizable(false);

		graphicsDevice.setFullScreenWindow(frame);

		if (displayMode != null && graphicsDevice.isDisplayChangeSupported()) {
			try {
				graphicsDevice.setDisplayMode(displayMode);
			} catch (Exception exc) {
				// Do Nothing
			}
		}
		frame.createBufferStrategy(2);
	}

	public Graphics2D getGraphics() {
		Window window = graphicsDevice.getFullScreenWindow();
		if (window != null) {
			BufferStrategy s = window.getBufferStrategy();
			return (Graphics2D) s.getDrawGraphics();
		} else {
			return null;
		}
	}

	public void update() {
		Window window = graphicsDevice.getFullScreenWindow();
		if (window != null) {
			BufferStrategy strategy = window.getBufferStrategy();
			if (!strategy.contentsLost()) {
				strategy.show();
			}
		}
		Toolkit.getDefaultToolkit().sync();
	}

	public Window getFullScreenWindow() {
		return graphicsDevice.getFullScreenWindow();
	}

	public int getWidth() {
		Window window = graphicsDevice.getFullScreenWindow();
		if (window != null) {
			return window.getWidth();
		} else {
			return 0;
		}
	}

	public int getHeight() {
		Window window = graphicsDevice.getFullScreenWindow();
		if (window != null) {
			return window.getHeight();
		} else {
			return 0;
		}
	}

	public void restoreScreen() {
		Window wwindow = graphicsDevice.getFullScreenWindow();
		if (wwindow != null) {
			wwindow.dispose();
		}
		graphicsDevice.setFullScreenWindow(null);
	}

	public BufferedImage createCompatibleImage(int width, int height, int transparency) {
		Window window = graphicsDevice.getFullScreenWindow();
		if (window != null) {
			GraphicsConfiguration gc = window.getGraphicsConfiguration();
			return gc.createCompatibleImage(width, height, transparency);
		}
		return null;
	}
}
