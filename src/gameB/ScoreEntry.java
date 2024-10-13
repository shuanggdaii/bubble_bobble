package gameB;

import java.io.Serializable;
import java.util.Date;

/**
 * ScoreEntry save the player name, score and date for the leader board
 */
public class ScoreEntry implements Comparable<ScoreEntry>, Serializable {
    private static final long serialVersionUID = 1L;
    private String playerName;
    private int score;
    private Date date;

    public ScoreEntry(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
        this.date = new Date();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return playerName + ": " + score + " (" + date + ")";
    }
}