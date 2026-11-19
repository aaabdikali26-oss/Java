package kz.project.totp;

public interface CodeGenerator {
    String generate(String base32Secret, long timeMillis);
}
