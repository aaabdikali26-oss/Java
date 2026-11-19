package kz.project.totp;

public abstract class AbstractAuthenticator {
    protected final CodeGenerator codeGenerator;

    protected AbstractAuthenticator(CodeGenerator codeGenerator) {
        if (codeGenerator == null) throw new IllegalArgumentException("Генератор қажет.");
        this.codeGenerator = codeGenerator;
    }

    public abstract String generateCode(String secret);
    public abstract boolean verifyCode(String secret, String userCode)
            throws InvalidCodeException;
}
