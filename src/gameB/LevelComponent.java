package gameB;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * LevelComponent has a timer and a current level. LevelComponent is responsible
 * for constructing levels and handling all appropriate GUI updates.
 *
 */
@SuppressWarnings("serial")
public class LevelComponent extends JComponent {

	protected static final int NUM_TILES = 20;
	protected static final int TILE_SIZE = 35;
	private static final int NUM_LEVELS = 10; // including game over menu
	private static final int WIN_CONDITION = 10000;

	// Level control stuff
	protected Level currentLevel;
	private Timer timer;
	private int levelIndex = 0;
	private boolean ranOnce = false; // makes sure Game over screen is only
	// loaded once

	// Keyboard input stuff
	private boolean leftKeyState = false;
	private boolean rightKeyState = false;
	private boolean upKeyState = false;

	// GUI stuff
	private Font font = new Font("Garamond", Font.BOLD, 20);
	private Color lightRed = new Color(233, 55, 72);
	private Color tangerine = new Color(237, 167, 4);

	public LevelComponent() {
		this.timer = new Timer(4, new ActionListener() { // 4 ms

			@Override
			public void actionPerformed(ActionEvent e) {
				timePassed();
			}
		});
	}

	/**
	 * Paints the current level and all of its collidable objects.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (this.currentLevel == null) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(this.font);

		// paint black background
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, 700, 700);

		// paint image for screen (only for 'main menu', 'game over', and 'game
		// won' screens)
		if (this.currentLevel.screen != null) {
			g2.drawImage(this.currentLevel.screen, 35, 35, null);
			g2.setColor(this.lightRed);
		}

		// paint all level objects
		for (Collidable c : this.currentLevel.collidables) {
			c.draw(g2);
		}

		// paint current lives and score
		g2.setColor(this.tangerine);
		g2.drawString("Lives: " + Integer.toString(this.currentLevel.hero.lives), 40, 30);
		g2.drawString("Score: " + Integer.toString((this.currentLevel.score)), 40, 50);
		g2.drawString("Level: " + Integer.toString((this.levelIndex)), 40, 70);

		// show user info
		User currentUser = Main.getCurrentUser();
		if (currentUser != null) {
			g2.setColor(Color.WHITE);
			g2.drawString("Player: " + currentUser.getNickname(), 40, 90);

			// show avatar
			String avatarPath = "avatars/" + currentUser.getAvatar();
			ImageIcon avatarIcon = new ImageIcon(avatarPath);
			Image avatarImage = avatarIcon.getImage();
			g2.drawImage(avatarImage, 40, 100, 30, 30, null);
		}
	}

	/**
	 *
	 * Draws a string onto a Graphics2D object, centered within the given
	 * rectangle.
	 *
	 * @param g2
	 * @param text
	 * @param rect
	 */
	public void drawStringCentered(Graphics2D g2, String text, Rectangle2D rect) {
		FontMetrics metrics = g2.getFontMetrics(this.font);
		double x = rect.getX() + (rect.getWidth() - metrics.stringWidth(text)) / 2;
		double y = rect.getY() + ((rect.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
		g2.drawString(text, (int) x, (int) y);
	}

	public boolean checkGameOver() {
		return this.currentLevel.gameOver;
	}

	/**
	 *
	 * Loads the appropriate level.
	 *
	 */
	public void loadLevel() {

		// main menu
		if (this.levelIndex == 0) {
			this.currentLevel = constructLevel(this.levelIndex);
			if (!this.timer.isRunning()) {
				this.timer.start();
				AudioManager.getInstance().play("audio/bubbleBobble.mid");
			}
		}
		// game won level
		else if (this.levelIndex == NUM_LEVELS + 1) {
			int oldScore = this.currentLevel.score;
			int oldLives = this.currentLevel.hero.lives;
			this.currentLevel = constructLevel(this.levelIndex);

			this.currentLevel.gameOver = true;
			this.ranOnce = true; // set ranOnce to true to simulate "ending
			// game"

			this.currentLevel.score += oldScore;
			this.currentLevel.hero.lives = oldLives;
		}

		// game over menu
		else if (this.levelIndex == NUM_LEVELS) {
			int oldScore = this.currentLevel.score;
			int oldLives = this.currentLevel.hero.lives;
			this.currentLevel = constructLevel(this.levelIndex);
			this.currentLevel.gameOver = true;
			this.currentLevel.score += oldScore;
			this.currentLevel.hero.lives = oldLives;
		}

		// Levels 1-9
		else {
			int oldScore = this.currentLevel.score;
			int oldLives = this.currentLevel.hero.lives;
			this.currentLevel = constructLevel(this.levelIndex);
			this.currentLevel.score += oldScore;
			this.currentLevel.hero.lives = oldLives;
		}
	}

	/**
	 * Updates the current level index based on the provided command:
	 *
	 * - command = 1: increment the level by one.
	 * - command = -1: decrement the level by one.
	 * - command = 0: load the main menu.
	 * - command = NUM_LEVELS: load the game over screen.
	 *
	 * Levels will loop through levels 1-9 when navigating with 'u' (up) and 'd' (down).
	 *
	 * @param command The command that determines the action to perform on the level index.
	 */

	public void changeLevel(int command) {

		// change to game over menu
		if (command == NUM_LEVELS) {
			this.levelIndex = NUM_LEVELS;
			this.loadLevel();
		}

		// change to main menu
		if (command == 0) {
			this.levelIndex = 0;
			this.ranOnce = false;
			this.currentLevel.gameOver = false;
			this.currentLevel.score = 0;
			this.loadLevel();
		}

		// increment level
		if (command == 1) {
			this.levelIndex++;

			// change to game won level if score is bigger than 10000 and completed level 9
			if (this.levelIndex > 9 && this.currentLevel.score > WIN_CONDITION) {
				this.levelIndex = NUM_LEVELS + 1;
				this.loadLevel();
			} else {
				if (this.levelIndex > NUM_LEVELS - 1) {
					this.levelIndex = 1;
				}
				this.loadLevel();
			}
		}

		// decrement level
		if (command == -1) {
			this.levelIndex--;
			if (this.levelIndex < 1) {
				this.levelIndex = NUM_LEVELS - 1;
			}
			this.loadLevel();
		}

	}

	/**
	 *
	 * Tells the current level to update. Repaints the component. TimePassed
	 * will be called every tick of the timer.
	 *
	 */
	public void timePassed() {
		this.handleMoveHero();
		this.currentLevel.timePassed();

		// check for a change in state of the game (passed level or game over)
		this.currentLevel.handleCheckGameState();

		// Check if level 9 is completed
		handleLevelNineCompletion();

		// decide if level should advance
		if (this.currentLevel.isReadyToChange && !this.checkGameOver() && this.currentLevel.screen == null) {
			this.changeLevel(1);
		}

		// ranOnce is set to true to prevent this if statement from running more
		// than once
		if (this.checkGameOver() && !this.ranOnce) {
			this.changeLevel(NUM_LEVELS);
			this.ranOnce = true;
			handleGameOver();
		}
		repaint(100);
	}

	/**
	 *
	 * Tells hero to move based on the button state of the arrow keys.
	 *
	 */
	public void handleMoveHero() {
		if (this.leftKeyState) {
			this.currentLevel.hero.move("Left");
		}
		if (this.rightKeyState) {
			this.currentLevel.hero.move("Right");
		}
		if (this.upKeyState) {
			this.currentLevel.hero.move("Up");
		}
	}

	/**
	 *
	 * Tells hero to shoot a bubble.
	 *
	 */
	public void handleBubble() {
		this.currentLevel.hero.shootBubble(false);
	}

	/**
	 *
	 * Tells hero to shoot a fast bubble.
	 *
	 */
	public void handleFastBubble() {
		this.currentLevel.hero.shootMultipleBubbles(10, true);
	}

	/**
	 *
	 * Set the state of the given key to the given state.
	 *
	 * @param key
	 * @param state
	 */
	public void setKeyState(String key, boolean state) {
		if (key.equals("Left")) {
			this.leftKeyState = state;
		}
		if (key.equals("Right")) {
			this.rightKeyState = state;
		}
		if (key.equals("Up")) {
			this.upKeyState = state;
		}
	}

	/**
	 *
	 * Takes in a level number and constructs the corresponding level using a
	 * file scanner.
	 *
	 * @param levelNum
	 * @return a newly constructed Level
	 */
	public Level constructLevel(int levelNum) {
		try {
			Level level = new Level();
			File level1 = new File("levels/level" + levelNum + ".txt");
			Scanner r1 = new Scanner(level1);
			int currentRow = 0;
			while (r1.hasNextLine()) {
				String currentLine = r1.nextLine();
				for (int i = 0; i < NUM_TILES; i++) {
					// tiny platform (1 space)
					if (currentLine.charAt(i) == '7') {
						level.addCollidable(new Platform(level, TILE_SIZE * i, TILE_SIZE * currentRow, TILE_SIZE,
								TILE_SIZE, (new ImageIcon("sprites/tinyPlatform.png")).getImage()));
					}
					// small platform (2 spaces)
					if (currentLine.charAt(i) == 'S') {
						level.addCollidable(new Platform(level, TILE_SIZE * i, TILE_SIZE * currentRow, 2 * TILE_SIZE,
								TILE_SIZE, (new ImageIcon("sprites/smallPlatform.png")).getImage()));
					}
					// medium platform (3 spaces)
					if (currentLine.charAt(i) == 'M') {
						level.addCollidable(new Platform(level, TILE_SIZE * i, TILE_SIZE * currentRow, 3 * TILE_SIZE,
								TILE_SIZE, (new ImageIcon("sprites/mediumPlatform.png")).getImage()));
					}
					// large platform (5 spaces)
					if (currentLine.charAt(i) == 'L') {
						level.addCollidable(new Platform(level, TILE_SIZE * i, TILE_SIZE * currentRow, 5 * TILE_SIZE,
								TILE_SIZE, (new ImageIcon("sprites/bigPlatform.png")).getImage()));
					}
					// floor platform
					if (currentLine.charAt(i) == 'F') {
						level.addCollidable(new Platform(level, TILE_SIZE * i, TILE_SIZE * currentRow,
								NUM_TILES * TILE_SIZE, TILE_SIZE, (new ImageIcon("sprites/floor.png")).getImage()));
					}
					// wall platform
					if (currentLine.charAt(i) == 'W') {
						level.addCollidable(new Platform(level, TILE_SIZE * i, TILE_SIZE * currentRow, TILE_SIZE,
								NUM_TILES * TILE_SIZE, (new ImageIcon("sprites/wall.png")).getImage()));
					}
					// hero
					if (currentLine.charAt(i) == 'H') {
						level.addCollidable(
								new Hero(level, TILE_SIZE * i, TILE_SIZE * currentRow, TILE_SIZE, TILE_SIZE));
					}
					// BubbleBuster
					if (currentLine.charAt(i) == 'B') {
						level.addCollidable(
								new BubbleBuster(level, TILE_SIZE * i, TILE_SIZE * currentRow, TILE_SIZE, TILE_SIZE));
					}
					// Incendo
					if (currentLine.charAt(i) == 'I') {
						level.addCollidable(
								new Incendo(level, TILE_SIZE * i, TILE_SIZE * currentRow, TILE_SIZE, TILE_SIZE));
					}
					if (currentLine.charAt(i) == 'E') {
						level.addCollidable(
								new EliteBoss(level, TILE_SIZE * i, TILE_SIZE * currentRow, TILE_SIZE, TILE_SIZE)
						);
					}
					// Game Over level
					if (currentLine.charAt(i) == 'T') {
						level.setLevelScreen((new ImageIcon("images/game_over.png")).getImage(), true);
					}
					// Main Menu level
					if (currentLine.charAt(i) == 't') {
						level.setLevelScreen((new ImageIcon("images/main_menu.png")).getImage(), false);
					}
					// Game Won level
					if (currentLine.charAt(i) == 'Z') {
						level.setLevelScreen((new ImageIcon("images/game_won.png")).getImage(), false);
					}
					// Spawn my boi Rascal
					if (currentLine.charAt(i) == 'R') {
						level.addCollidable(
								new Fruit(level, TILE_SIZE * i, TILE_SIZE * currentRow, TILE_SIZE, TILE_SIZE, 69));
					}
				}
				currentRow++;
			}
			r1.close();
			return level;

		} catch (IOException e) {
			System.err.println("File not found");

		}
		return null;
	}

	private void handleGameOver() {
		if (this.currentLevel.gameOver) {
			String playerName = Main.getCurrentUser().getNickname();
			int finalScore = this.currentLevel.score;
			int finalLevel = this.levelIndex;

			GameRecord record = new GameRecord(finalScore, finalLevel);
			Main.getUserManager().addGameRecord(playerName, record);

			Main.getLeaderBoard().addScore(playerName, finalScore);

			showLeaderBoard();
		}
	}

	private void handleLevelNineCompletion() {
		if (this.levelIndex == 9 && this.currentLevel.isReadyToChange) {
			String playerName = JOptionPane.showInputDialog("Congratulations! You've completed level 9. Enter your name:");
			if (playerName != null && !playerName.trim().isEmpty()) {
				Main.getLeaderBoard().addScore(playerName, this.currentLevel.score);
			}
			showLeaderBoard();
		}
	}

	private void showLeaderBoard() {
		StringBuilder sb = new StringBuilder("LeaderBoard:\n");
		for (ScoreEntry entry : Main.getLeaderBoard().getEntries()) {
			sb.append(entry.toString()).append("\n");
		}
		JOptionPane.showMessageDialog(this, sb.toString());
	}
}