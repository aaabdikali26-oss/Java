package kz.project.totp;

import org.apache.commons.codec.binary.Base32;

import java.security.SecureRandom;

public class SecretKeyManager {
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateSecret() {
        byte[] secretBytes = new byte[20]; // 160 биттік құпия кілт
        secureRandom.nextBytes(secretBytes);
        return new Base32().encodeToString(secretBytes).replace("=", "");
    }
}
