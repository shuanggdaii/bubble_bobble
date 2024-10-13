package gameB;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;
import java.util.LinkedList;

import javax.swing.ImageIcon;

/**
 * The hero shoots a bubble that captures monsters upon contact. 
 * Once a monster is trapped, the bubble transports it to the top of the screen. 
 * After staying at the top for a certain amount of time, the bubble spawns fruit. 
 * If the bubble remains untouched for too long, it will eventually pop.
 */

public class Bubble extends Projectile {

	private boolean reachedTop = false;
	private boolean hasFruit = false;
	private boolean isFast;
	private static final double NORMAL_SPEED = 3;
	private static final double FAST_SPEED = 6;
	private static final int TRAIL_LENGTH = 10;
	private LinkedList<Point> trail;

	public Bubble(Level level, int x, int y, int dir, boolean isFast) {
		super(level, x, y, dir);
		this.color = Color.CYAN;
		this.scoreValue = 10;
		this.sprites = new Image[4];
		this.sprites[0] = (new ImageIcon("sprites/bubble.gif")).getImage();
		this.sprites[1] = (new ImageIcon("sprites/bubble6.png")).getImage();
		this.sprites[2] = (new ImageIcon("sprites/fruit_bubble.png")).getImage();
		this.sprites[3] = (new ImageIcon("sprites/fast_bubble.gif")).getImage(); // fast bubble
		this.isFast = isFast;
		this.sprite = isFast ? this.sprites[3] : this.sprites[0];
		this.dx = isFast ? FAST_SPEED * dir : NORMAL_SPEED * dir;
		this.trail = new LinkedList<>();
	}

	/**
	 * Bubble moves horizontally for a certain distance, then moves vertically
	 * to the top of the screen.
	 */
	@Override
	public void updatePosition() {
		super.updatePosition();

		// update position
		trail.addFirst(new Point(this.positionX, this.positionY));
		if (trail.size() > TRAIL_LENGTH) {
			trail.removeLast();
		}

		if (this.dx == 0 && !this.reachedTop) {
			this.dy = -DX;
			this.sprite = this.sprites[1];
		}
		if (this.positionY <= 0 && !this.reachedTop) {
			this.reachedTop = true;
			this.dy = 0;

			// Starts growing fruit
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Bubble.this.hasFruit = true;
					Bubble.this.sprite = Bubble.this.sprites[2];
				}
			}, 5000);

			// Bubble pops after a period
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Bubble.this.die();
				}
			}, 8000);
		}
	}

	@Override
	public void addToList() {
		this.level.bubbles.add(this);
	}

	/**
	 * Spawns fruit in the level if the hero collides with the bubble 
	 * and the bubble "contains fruit."
	 */
	public void collideWithHero() {
		if (this.hasFruit) {
			this.die();
			int x = this.positionX;
			int y = this.positionY;
			this.level.addCollidable(new Fruit(this.level, x, y, SIZE, SIZE, this.scoreValue));
		} else {
			this.die();
		}
	}

	public boolean isFast() {
		return this.isFast;
	}

	/**
	 * Override the draw method to include the trail
	 */
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);

		g.setColor(new Color(0, 255, 255, 128));
		Point prevPoint = null;
		for (Point point : trail) {
			if (prevPoint != null) {
				g.drawLine(prevPoint.x + SIZE/2, prevPoint.y + SIZE/2,
						point.x + SIZE/2, point.y + SIZE/2);
			}
			prevPoint = point;
		}
	}
}