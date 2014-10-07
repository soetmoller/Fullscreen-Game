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

	private GraphicsDevice vc;

	// gives vc access to monitor screen
	public ScreenManager() {
		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		vc = env.getDefaultScreenDevice();
	}

	public DisplayMode[] getCompatibleDisplayModes() {
		return vc.getDisplayModes();
	}

	// compares DM passed in to vc DM and see if they match
	public DisplayMode findFirstCompatibleDisplayMode(DisplayMode modes[]) {
		DisplayMode goodModes[] = vc.getDisplayModes();
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
		return vc.getDisplayMode();
	}

	public boolean displayModesMatch(DisplayMode m1, DisplayMode m2) {
		if (m1.getWidth() != m2.getWidth() || m1.getHeight() != m2.getHeight()) {
			return false;
		}
		if (m1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
				&& m2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
				&& m1.getBitDepth() != m2.getBitDepth()) {
			return false;
		}
		if (m1.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
				&& m2.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
				&& m1.getRefreshRate() != m2.getRefreshRate()) {
			return false;
		}

		return true;
	}

	public void setFullScreen(DisplayMode dm) {
		JFrame f = new JFrame();
		f.setUndecorated(true);
		f.setIgnoreRepaint(true);
		f.setResizable(false);

		vc.setFullScreenWindow(f);

		if (dm != null && vc.isDisplayChangeSupported()) {
			try {
				vc.setDisplayMode(dm);

			} catch (Exception exc) {
			}
		}
		f.createBufferStrategy(2);
	}

	// we will set Graphics object = to this
	public Graphics2D getGraphics() {
		Window w = vc.getFullScreenWindow();
		if (w != null) {
			BufferStrategy s = w.getBufferStrategy();
			return (Graphics2D) s.getDrawGraphics();
		} else {
			return null;
		}
	}

	// updates graphic
	public void update() {
		Window w = vc.getFullScreenWindow();
		if (w != null) {
			BufferStrategy s = w.getBufferStrategy();
			if (!s.contentsLost()) {
				s.show();
			}
		}
		Toolkit.getDefaultToolkit().sync();
	}

	public Window getFullScreenWindow() {
		return vc.getFullScreenWindow();
	}

	public int getWidth() {
		Window w = vc.getFullScreenWindow();
		if (w != null) {
			return w.getWidth();
		} else {
			return 0;
		}
	}

	public int getHeight() {
		Window w = vc.getFullScreenWindow();
		if (w != null) {
			return w.getHeight();
		} else {
			return 0;
		}
	}

	public void restoreScreen() {
		Window w = vc.getFullScreenWindow();
		if (w != null) {
			w.dispose();
		}
		vc.setFullScreenWindow(null);
	}

	public BufferedImage createCompatibleImage(int w, int h, int t) {
		Window window = vc.getFullScreenWindow();
		if (window != null) {
			GraphicsConfiguration gc = window.getGraphicsConfiguration();
			return gc.createCompatibleImage(w, h, t);
		}
		return null;
	}
}
