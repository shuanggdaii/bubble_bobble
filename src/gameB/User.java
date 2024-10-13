package gameB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * User class has nickname, avatar, and all game records.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nickname;
    private String avatar; // save avatr
    private List<GameRecord> gameRecords;

    public User(String nickname, String avatar) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.gameRecords = new ArrayList<>();
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public List<GameRecord> getGameRecords() {
        return gameRecords;
    }

    public void addGameRecord(GameRecord record) {
        gameRecords.add(record);
    }
}