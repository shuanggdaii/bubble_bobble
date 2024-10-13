package gameB;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USER_FILE = "users.ser";
    private Map<String, User> users;

    public UserManager() {
        users = new HashMap<>();
        loadUsers();
    }

    public User getUser(String nickname) {
        return users.get(nickname);
    }

    public User createUser(String nickname, String avatar) {
        if (!users.containsKey(nickname)) {
            User newUser = new User(nickname, avatar);
            users.put(nickname, newUser);
            saveUsers();
            return newUser;
        }
        return null;
    }

    public void addGameRecord(String nickname, GameRecord record) {
        User user = users.get(nickname);
        if (user != null) {
            user.addGameRecord(record);
            saveUsers();
        }
    }

    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            // if file doesn't exist, use a new hash map
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}