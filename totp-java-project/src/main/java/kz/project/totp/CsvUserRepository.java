package kz.project.totp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CsvUserRepository implements UserRepository {
    private final Path file;
    private final Map<Integer, User> usersById = new HashMap<>();

    public CsvUserRepository(String fileName) {
        this.file = Paths.get(fileName);
        load();
    }

    private void load() {
        try {
            Path parent = file.getParent();
            if (parent != null) Files.createDirectories(parent);
            if (!Files.exists(file)) {
                Files.createFile(file);
                return;
            }

            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank() || line.startsWith("id,")) continue;
                String[] parts = line.split(",", -1);
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    usersById.put(id, new User(id, parts[1], parts[2]));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("CSV файлын оқу қатесі: " + e.getMessage(), e);
        }
    }

    @Override
    public void save(User user) throws IOException {
        usersById.put(user.getId(), user);
        List<String> lines = new ArrayList<>();
        lines.add("id,username,secret");
        for (User current : usersById.values()) {
            lines.add(current.getId() + "," + current.getUsername()
                    + "," + current.getSecret());
        }
        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public User findById(int id) {
        return usersById.get(id);
    }

    @Override
    public User findByUsername(String username) {
        return usersById.values().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst().orElse(null);
    }

    @Override
    public int nextId() {
        return usersById.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    }
}
