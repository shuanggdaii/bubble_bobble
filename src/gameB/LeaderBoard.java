package gameB;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * LeaderBoard create a ranking of 10 top game records.
 */
public class LeaderBoard {
    private static final int MAX_ENTRIES = 10;
    private List<ScoreEntry> entries;
    private static final String FILENAME = "leaderboard.ser";

    public LeaderBoard() {
        entries = new ArrayList<>();
        loadLeaderBoard();
    }

    public void addScore(String playerName, int score) {
        entries.add(new ScoreEntry(playerName, score));
        entries = entries.stream()
                .sorted()
                .collect(Collectors.toList());
        saveLeaderBoard();
    }

    public List<ScoreEntry> getEntries() {
        return entries.stream()
                .limit(MAX_ENTRIES)
                .collect(Collectors.toList());
    }

    private void saveLeaderBoard() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
            oos.writeObject(entries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadLeaderBoard() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
            entries = (List<ScoreEntry>) ois.readObject();
        } catch (FileNotFoundException e) {
            // It's okay if the file doesn't exist yet
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}