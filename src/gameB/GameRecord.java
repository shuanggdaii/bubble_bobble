package gameB;
import java.io.Serializable;


/**
 * GameRecord implements Serializable, 
 * and memories score, level and date for current user.
 */
import java.util.Date;

public class GameRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int score;
    private int level;
    private Date date;

    public GameRecord(int score, int level) {
        this.score = score;
        this.level = level;
        this.date = new Date();
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Score: " + score + ", Level: " + level + ", Date: " + date;
    }
}