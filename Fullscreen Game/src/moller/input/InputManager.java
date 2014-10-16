package moller.input;

/********************************************************************************************************************************************************************************************
 * InputManager.java implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
 * 
 * The InputManager manages input of key and mouse events.
 * Events are mapped to GameActions
 * 
 * Made By: Emil Möller
 * Email: emilmol@kth.se
 *******************************************************************************************************************************************************************************************/

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class InputManager implements KeyListener, MouseListener,
		MouseMotionListener, MouseWheelListener {

	public static final Cursor INVISIBLE_CURSOR = Toolkit.getDefaultToolkit()
			.createCustomCursor(Toolkit.getDefaultToolkit().getImage(""),
					new Point(0, 0), "invisible");

	// mouse codes
	public static final int MOUSE_MOVE_LEFT = 0;
	public static final int MOUSE_MOVE_RIGHT = 1;
	public static final int MOUSE_MOVE_UP = 2;
	public static final int MOUSE_MOVE_DOWN = 3;
	public static final int MOUSE_WHEEL_UP = 4;
	public static final int MOUSE_WHEEL_DOWN = 5;
	public static final int MOUSE_BUTTON_1 = 6;
	public static final int MOUSE_BUTTON_2 = 7;
	public static final int MOUSE_BUTTON_3 = 8;

	private static final int NUM_MOUSE_CODES = 9;

	// key codes are defined in java.awt.KeyEvent.
	// most of the codes (except for some rare ones like
	// "alt graph") are less than 600.
	private static final int NUM_KEY_CODES = 600;

	private GameAction[] keyActions = new GameAction[NUM_KEY_CODES];
	private GameAction[] mouseActions = new GameAction[NUM_MOUSE_CODES];

	private Point mouseLocation;
	private Point centerLocation;
	private Component component;
	private Robot robot;
	private boolean isRecentering;

	public InputManager(Component component) {
		this.component = component;
		mouseLocation = new Point();
		centerLocation = new Point();

		component.addKeyListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
		component.setFocusTraversalKeysEnabled(false);
	}

	public void setCursor(Cursor cursor) {
		component.setCursor(cursor);
	}
	
	public void setMouseLockedInCenter(boolean lockInCenter) {
		if (lockInCenter == isMouseLockedInCenter()) {
			return;
		}
		if (lockInCenter) {
			try {
				robot = new Robot();
				recenterMouse();
			} catch (AWTException ex) {
				robot = null;
			}
		} else {
			robot = null;
		}
	}

	public boolean isMouseLockedInCenter() {
		return (robot != null);
	}

	public void mapActionToKey(GameAction gameAction, int keyCode) {
		keyActions[keyCode] = gameAction;
	}

	public void mapActionToMouse(GameAction gameAction, int mouseCode) {
		mouseActions[mouseCode] = gameAction;
	}

	public void clearActionMapped(GameAction gameAction) {
		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] == gameAction) {
				keyActions[i] = null;
			}
		}
		for (int i = 0; i < mouseActions.length; i++) {
			if (mouseActions[i] == gameAction) {
				mouseActions[i] = null;
			}
		}
		gameAction.reset();
	}

	public List<String> getKeyNamesMappedToAction(GameAction gameAction) {
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] == gameAction) {
				list.add(getKeyName(i));
			}
		}

		for (int i = 0; i < mouseActions.length; i++) {
			if (mouseActions[i] == gameAction) {
				list.add(getMouseCodeName(i));
			}
		}
		return list;
	}

	public void resetAllGameActions() {
		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] != null) {
				keyActions[i].reset();
			}
		}

		for (int i = 0; i < mouseActions.length; i++) {
			if (mouseActions[i] != null) {
				mouseActions[i].reset();
			}
		}
	}

	public static String getKeyName(int keyCode) {
		return KeyEvent.getKeyText(keyCode);
	}

	public static String getMouseCodeName(int mouseCode) {
		switch (mouseCode) {
		case MOUSE_MOVE_LEFT:
			return "Mouse Left";
		case MOUSE_MOVE_RIGHT:
			return "Mouse Right";
		case MOUSE_MOVE_UP:
			return "Mouse Up";
		case MOUSE_MOVE_DOWN:
			return "Mouse Down";
		case MOUSE_WHEEL_UP:
			return "Mouse Wheel Up";
		case MOUSE_WHEEL_DOWN:
			return "Mouse Wheel Down";
		case MOUSE_BUTTON_1:
			return "Mouse Button 1";
		case MOUSE_BUTTON_2:
			return "Mouse Button 2";
		case MOUSE_BUTTON_3:
			return "Mouse Button 3";
		default:
			return "Unknown mouse code " + mouseCode;
		}
	}

	public int getMouseX() {
		return mouseLocation.x;
	}

	public int getMouseY() {
		return mouseLocation.y;
	}

	// Note that use of the Robot class may not be available on all platforms.
	private synchronized void recenterMouse() {
		if (robot != null && component.isShowing()) {
			centerLocation.x = component.getWidth() / 2;
			centerLocation.y = component.getHeight() / 2;
			SwingUtilities.convertPointToScreen(centerLocation, component);
			isRecentering = true;
			robot.mouseMove(centerLocation.x, centerLocation.y);
		}
	}

	private GameAction getKeyAction(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode < keyActions.length) {
			return keyActions[keyCode];
		} else {
			return null;
		}
	}

	public static int getMouseButtonCode(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1:
			return MOUSE_BUTTON_1;
		case MouseEvent.BUTTON2:
			return MOUSE_BUTTON_2;
		case MouseEvent.BUTTON3:
			return MOUSE_BUTTON_3;
		default:
			return -1;
		}
	}

	private GameAction getMouseButtonAction(MouseEvent e) {
		int mouseCode = getMouseButtonCode(e);
		if (mouseCode != -1) {
			return mouseActions[mouseCode];
		} else {
			return null;
		}
	}

	public void keyPressed(KeyEvent e) {
		GameAction gameAction = getKeyAction(e);
		if (gameAction != null) {
			gameAction.press();
		}
		e.consume();
	}

	public void keyReleased(KeyEvent e) {
		GameAction gameAction = getKeyAction(e);
		if (gameAction != null) {
			gameAction.release();
		}
		e.consume();
	}

	public void keyTyped(KeyEvent e) {
		e.consume();
	}

	public void mousePressed(MouseEvent e) {
		GameAction gameAction = getMouseButtonAction(e);
		if (gameAction != null) {
			gameAction.press();
		}
	}

	public void mouseReleased(MouseEvent e) {
		GameAction gameAction = getMouseButtonAction(e);
		if (gameAction != null) {
			gameAction.release();
		}
	}

	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	public void mouseEntered(MouseEvent e) {
		mouseMoved(e);
	}

	public void mouseExited(MouseEvent e) {
		mouseMoved(e);
	}

	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	public synchronized void mouseMoved(MouseEvent e) {
		if (isRecentering && centerLocation.x == e.getX()
				&& centerLocation.y == e.getY()) {
			isRecentering = false;
		} else {
			int dx = e.getX() - mouseLocation.x;
			int dy = e.getY() - mouseLocation.y;
			mouseDistanceMoved(MOUSE_MOVE_LEFT, MOUSE_MOVE_RIGHT, dx);
			mouseDistanceMoved(MOUSE_MOVE_UP, MOUSE_MOVE_DOWN, dy);

			if (isMouseLockedInCenter()) {
				recenterMouse();
			}
		}

		mouseLocation.x = e.getX();
		mouseLocation.y = e.getY();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseDistanceMoved(MOUSE_WHEEL_UP, MOUSE_WHEEL_DOWN, e.getWheelRotation());
	}

	private void mouseDistanceMoved(int codeNeg, int codePos, int amount) {
		GameAction gameAction;
		if (amount < 0) {
			gameAction = mouseActions[codeNeg];
		} else {
			gameAction = mouseActions[codePos];
		}
		if (gameAction != null) {
			gameAction.press(Math.abs(amount));
			gameAction.release();
		}
	}

}
