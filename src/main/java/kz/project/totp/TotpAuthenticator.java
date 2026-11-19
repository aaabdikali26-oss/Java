package kz.project.totp;

public class TotpAuthenticator extends AbstractAuthenticator {
    private static final long TIME_STEP_MILLIS = 30_000L;
    private static final int ALLOWED_TIME_DRIFT = 1;

    public TotpAuthenticator(CodeGenerator codeGenerator) {
        super(codeGenerator);
    }

    @Override
    public String generateCode(String secret) {
        return codeGenerator.generate(secret, System.currentTimeMillis());
    }

    @Override
    public boolean verifyCode(String secret, String userCode)
            throws InvalidCodeException {
        if (userCode == null || !userCode.matches("\\d{6}")) {
            throw new InvalidCodeException("Код дәл 6 саннан тұруы керек.");
        }

        long currentStep = System.currentTimeMillis() / TIME_STEP_MILLIS;
        for (long offset = -ALLOWED_TIME_DRIFT;
             offset <= ALLOWED_TIME_DRIFT; offset++) {
            long testTime = (currentStep + offset) * TIME_STEP_MILLIS;
            String expected = codeGenerator.generate(secret, testTime);
            if (constantTimeEquals(expected, userCode)) {
                return true;
            }
        }
        return false;
    }

    public int getRemainingSeconds() {
        long nowSeconds = System.currentTimeMillis() / 1000L;
        return (int) (30 - (nowSeconds % 30));
    }

    private boolean constantTimeEquals(String first, String second) {
        int difference = first.length() ^ second.length();
        for (int i = 0; i < Math.min(first.length(), second.length()); i++) {
            difference |= first.charAt(i) ^ second.charAt(i);
        }
        return difference == 0;
    }
}
