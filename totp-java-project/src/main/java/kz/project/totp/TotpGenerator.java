package kz.project.totp;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Locale;

public class TotpGenerator implements CodeGenerator {
    private static final long TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;

    @Override
    public String generate(String base32Secret, long timeMillis) {
        try {
            if (base32Secret == null || base32Secret.isBlank()) {
                throw new IllegalArgumentException("Құпия кілт бос.");
            }
            long counter = (timeMillis / 1000L) / TIME_STEP_SECONDS;
            byte[] key = new Base32().decode(base32Secret);
            byte[] counterBytes = ByteBuffer.allocate(8).putLong(counter).array();

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(counterBytes);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary =
                    ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            int otp = binary % 1_000_000;
            return String.format(Locale.ROOT, "%06d", otp);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("TOTP кодын жасау мүмкін болмады.", e);
        }
    }
}
