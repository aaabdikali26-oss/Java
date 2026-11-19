package kz.project.totp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TotpGeneratorTest {
    @Test
    void generatesRfc6238SixDigitCodeForKnownTestVector() {
        // RFC 6238 SHA1 тест кілті; 59 секундта күтілетін 6 таңбалы код — 287082.
        String secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";
        TotpGenerator generator = new TotpGenerator();

        assertEquals("287082", generator.generate(secret, 59_000L));
    }

    @Test
    void generatedCodeAlwaysHasSixDigits() {
        String secret = new SecretKeyManager().generateSecret();
        String code = new TotpGenerator().generate(secret, System.currentTimeMillis());

        assertTrue(code.matches("\\d{6}"));
    }
}
