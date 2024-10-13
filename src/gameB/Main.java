package gameB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * Main opens a JFrame center screen and loads the main menu screen on the GUI.
 * All key listeners are attached to the frame. User input controls the flow of
 * the game from there. The frame closes the program upon exit. Game is won win
 * 10000 points have been achieved and level 9 is beaten.
 *
 */

public class Main {
	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int SCREEN_WIDTH = SCREEN_SIZE.width;
	public static final int SCREEN_HEIGHT = SCREEN_SIZE.height;
	public static final int TITLE_HEIGHT = 50;
	public static final int PANEL_SIZE = 700;
	public static final int PANEL_X = (SCREEN_WIDTH / 2) - (PANEL_SIZE / 2);
	public static final int PANEL_Y = (SCREEN_HEIGHT / 2) - (PANEL_SIZE / 2) - TITLE_HEIGHT;
	public static final int NUM_TILES = 20;
	public static final int TILE_SIZE = PANEL_SIZE / NUM_TILES;

	private static LeaderBoard leaderBoard = new LeaderBoard();
	private static UserManager userManager = new UserManager();
	private static User currentUser;

	/**
	 *
	 * Program execution begins here.
	 *
	 * @param None
	 */
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String nickname = JOptionPane.showInputDialog("Enter your nickname:");
		String avatar = chooseAvatar();

		currentUser = userManager.getUser(nickname);
		if (currentUser == null) {
			currentUser = userManager.createUser(nickname, avatar);
		}

		JFrame mainFrame = new JFrame("BubbleBobble");
		mainFrame.setLocation(PANEL_X, PANEL_Y);
		mainFrame.setFocusable(true);

		final LevelComponent component = new LevelComponent();
		component.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
		mainFrame.add(component, BorderLayout.CENTER);

		JPanel titlePanel = new JPanel();
		titlePanel.setPreferredSize(new Dimension(PANEL_SIZE, TITLE_HEIGHT));
		titlePanel.setBackground(new Color(93, 79, 56));

		JLabel title = new JLabel("BubbleBobble Game");
		title.setFont(new Font("Bookman Old Style", Font.BOLD, 30));
		title.setForeground(new Color(239, 163, 43));

		titlePanel.add(title);
		mainFrame.add(titlePanel, BorderLayout.NORTH);
		
		/**
		 * create a panel with exit,leader board, user info  buttons
		 */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(PANEL_SIZE, TITLE_HEIGHT));
		buttonPanel.setBackground(new Color(93, 79, 56));

		mainFrame.add(buttonPanel, BorderLayout.SOUTH);

		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(e -> System.exit(0));
		buttonPanel.add(exitButton);

		JButton leaderboardButton = new JButton("View Leaderboard");
		leaderboardButton.addActionListener(e -> showLeaderBoard());
		buttonPanel.add(leaderboardButton);

		JButton userInfoButton = new JButton("User Info");
		userInfoButton.addActionListener(e -> showUserInfo());
		buttonPanel.add(userInfoButton);

		component.loadLevel();
		mainFrame.addKeyListener(new BubbleKeyListener(component));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	/**
	 * set user info, avatar, leader board
	 */
	private static void showUserInfo() {
		if (currentUser != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("User Information:\n\n");
			sb.append("Username: ").append(currentUser.getNickname()).append("\n");
			sb.append("Avatar: ").append(currentUser.getAvatar()).append("\n\n");
			sb.append("Game Records:\n");

			List<GameRecord> records = currentUser.getGameRecords();
			if (records.isEmpty()) {
				sb.append("No game records yet.\n");
			} else {
				for (GameRecord record : records) {
					sb.append(record.toString()).append("\n");
				}
			}

			ImageIcon avatarIcon = new ImageIcon("avatars/" + currentUser.getAvatar());
			Image image = avatarIcon.getImage();
			Image newimg = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
			avatarIcon = new ImageIcon(newimg);

			JOptionPane.showMessageDialog(null, sb.toString(), "User Information",
					JOptionPane.INFORMATION_MESSAGE, avatarIcon);
		} else {
			JOptionPane.showMessageDialog(null, "No user logged in.");
		}
	}

	private static String chooseAvatar() {
		String[] options = {"Upload your own", "avatar1.png", "avatar2.png", "avatar3.png"};
		String choice = (String) JOptionPane.showInputDialog(null, "Choose your avatar:",
				"Avatar Selection", JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);

		if ("Upload your own".equals(choice)) {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "gif");
			fileChooser.setFileFilter(filter);

			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				String fileName = "avatar_" + System.currentTimeMillis() + "_" + selectedFile.getName();
				Path copied = Paths.get("avatars", fileName);
				try {
					Files.createDirectories(copied.getParent());
					Files.copy(selectedFile.toPath(), copied, StandardCopyOption.REPLACE_EXISTING);
					return fileName;
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Failed to save avatar. Using default.");
					return "avatar1.png";
				}
			} else {
				return "avatar1.png"; // Default if user cancels upload
			}
		} else {
			return choice;
		}
	}

	private static void showLeaderBoard() {
		StringBuilder sb = new StringBuilder("LeaderBoard:\n");
		for (ScoreEntry entry : leaderBoard.getEntries()) {
			sb.append(entry.toString()).append("\n");
		}
		JOptionPane.showMessageDialog(null, sb.toString());
	}

	public static LeaderBoard getLeaderBoard() {
		return leaderBoard;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static UserManager getUserManager() {
		return userManager;
	}

	/**
	 *
	 * BubbleKeyListener listens to all key events, then calls the
	 * specific functions in the level component.
	 *
	 */
	private static class BubbleKeyListener implements KeyListener {

		private LevelComponent comp;

		public BubbleKeyListener(LevelComponent comp) {
			this.comp = comp;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				this.comp.setKeyState("Right", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				this.comp.setKeyState("Left", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				this.comp.setKeyState("Up", true);
				this.comp.handleMoveHero();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			char key = e.getKeyChar();
			if (key == 'u' && !this.comp.checkGameOver()) {
				this.comp.changeLevel(1);
			}
			if (key == 'd' && !this.comp.checkGameOver()) {
				this.comp.changeLevel(-1);
			}
			if (key == 'p') {
				this.comp.currentLevel.changePause();
			}
			if (key == 'm') {
				this.comp.changeLevel(0);
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				this.comp.setKeyState("Up", false);
				this.comp.handleMoveHero();
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				this.comp.setKeyState("Left", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				this.comp.setKeyState("Right", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				this.comp.handleBubble();
			}
			if (e.getKeyCode() == KeyEvent.VK_L) {
				this.comp.handleFastBubble();
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// Not used
		}
	}
}