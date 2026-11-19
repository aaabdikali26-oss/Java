package kz.project.totp;

public class User {
    private final int id;
    private final String username;
    private final String secret;

    public User(int id, String username, String secret) {
        if (id < 1) throw new IllegalArgumentException("ID 1-ден басталуы керек.");
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Пайдаланушы аты бос болмауы керек.");
        }
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("Құпия кілт бос болмауы керек.");
        }
        this.id = id;
        this.username = username;
        this.secret = secret;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getSecret() { return secret; }
}
