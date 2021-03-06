import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.EventObject;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import engine.CollisionEngine;
import engine.CollisionEngine.CollisionEvent;
import engine.Engine;

public class FinalProject extends Engine {
	
	// The name of the game.
	private static final String NAME = "Pong";
	
	// Predefined list of keys to assign to players.
	private static final int[][] PLAYER_KEYS = {
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			// { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
			{ KeyEvent.VK_UP, KeyEvent.VK_DOWN },
	/*
	 * { KeyEvent.VK_Q, KeyEvent.VK_A }, { KeyEvent.VK_W, KeyEvent.VK_S }, {
	 * 
	 * KeyEvent.VK_E, KeyEvent.VK_D }, { KeyEvent.VK_R, KeyEvent.VK_F }, {
	 * KeyEvent.VK_T, KeyEvent.VK_G },
	 */

	};
	
	// Delays the start of the game with a 5 second count down.
	private static final int COUNTDOWN_DELAY = 5;

	// The point on the screen that the Pong ball should start at.
	private static final Point2D BALL_START_POSITION = new Point2D.Double(0, 0); // Center
																					// of
																					// screen
	
	// The number of players to use.
	private final int nPlayers;

	public Paddle[] paddles;
	public GoalLine[] goalLines;

	private Ball ball;
	
	// This timer is used to provide a delay at the beginning of the game.
	private Timer countdownTimer;
	private int countdownValue;

	/**
	 * Constructor
	 */
	public FinalProject(int nPlayers) {
		// Initialize the game engine with a name.
		super(NAME);

		this.nPlayers = nPlayers;

		System.out.println("Starting a " + nPlayers + " player game.");

		// Initialize the game
		initialize();
	}

	/**
	 * Initialize all game objects.
	 */
	public void initialize() {
		super.initialize();

		ball = new Ball(BALL_START_POSITION);

		addObject(ball);

		initializePlayers();
	}
	
	public void start() {
		super.start();
		
		reset();
	}

	protected void onUpdate(long tick) {}

	protected void onRender(Graphics2D g) {
		
		// If the count down is in-progress display the countdown value. 
		if (countdownValue > 0) {
			g.setColor(Color.GREEN);
			g.scale(1, -1);
			g.translate(-3, 3);
			g.drawString(Integer.toString(countdownValue), 0, 0);
		}
	}
	
	/**
	 * Splits the circumference of a circle into `2*N_PLAYERS - 1` points to act
	 * as the boundaries for `N_PLAYERS` player paddles, arranged in a polygon
	 * with `N_PLAYERS` sides.
	 */
	private void initializePlayers() {

		final double angleIncrement = 2 * Math.PI / nPlayers;

		paddles = new Paddle[nPlayers];
		goalLines = new GoalLine[nPlayers];

		Point2D a, b;

		for (int i = 0; i < nPlayers; i++) {

			a = new Point2D.Double(WINDOW_SCALE * Math.cos(i * angleIncrement),
					WINDOW_SCALE * Math.sin(i * angleIncrement));
			b = new Point2D.Double(WINDOW_SCALE
					* Math.cos((i + 1) * angleIncrement), WINDOW_SCALE
					* Math.sin((i + 1) * angleIncrement));

			paddles[i] = new Paddle(a, b, PLAYER_KEYS[i][0], PLAYER_KEYS[i][1]);

			goalLines[i] = new GoalLine(a, b);

			addObject(goalLines[i]);
			addObject(paddles[i]);

			collisionEngine.addListener(paddles[i], ball,
					new engine.events.Listener() {

						@Override
						public boolean handle(EventObject e_) {
							CollisionEngine.CollisionEvent e = (CollisionEvent) e_;

							ball.onCollidePaddle((Paddle) e.getSource());

							return false;
						}
					});

			collisionEngine.addListener(goalLines[i], ball,
					new engine.events.Listener() {

						@Override
						public boolean handle(EventObject e_) {
							CollisionEngine.CollisionEvent e = (CollisionEvent) e_;

							ball.onCollideGoal((GoalLine) e.getSource());

							return false;
						}
					});

		}
	}
	
	public void reset() {
		countdownValue = COUNTDOWN_DELAY;
		
		final FinalProject this_ = this;
		
		ActionListener timerAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				countdownValue--;
				
				if (countdownValue == 0) {
					countdownTimer.stop();
					eh.dispatchEvent(new GameStartEvent(this_));
				}
			}
		};
		
		countdownTimer = new Timer(1000, timerAction);
		countdownTimer.start();
	}
	
	public static void main(String[] args) {
		String inputString;
		int nPlayers = 0;

		while (nPlayers < 2) {
			inputString = JOptionPane
					.showInputDialog("Input the number of players:");
	
			try {
				nPlayers = Integer.parseInt(inputString);
			} catch (NumberFormatException e) {
				return;
			}
		}
		
		new FinalProject(nPlayers).start();
	}
	
	public static class GameStartEvent extends engine.events.Event {
		
		public GameStartEvent(Object source) {
			super(source);
		}
	}
}
