package gameB;
import java.awt.Color;

import javax.swing.ImageIcon;

/**
 * The Fireball is shot by an Incendo monster and travels a set distance before
 * disappearing. Fireballs interact with the hero in the same manner as monsters
 * do upon collision.
 */

public class Fireball extends Projectile {

	public Fireball(Level level, int x, int y, int dir) {
		super(level, x, y, dir);
		this.color = Color.RED;
		if (this.direction == -1) {
			this.sprite = (new ImageIcon("sprites/fireball_left.png")).getImage();
		} else {
			this.sprite = (new ImageIcon("sprites/fireball_right.png")).getImage();
		}
	}

	@Override
	public void updatePosition() {
		super.updatePosition();
		if (this.dx == 0) {
			this.die();
		}
	}

	@Override
	public void addToList() {
		this.level.fireballs.add(this);
	}
}
