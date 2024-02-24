package com.william.notix.services;

import com.william.notix.utils.Bcrypt;
import java.security.SecureRandom;
import java.util.regex.Pattern;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class EncoderService {

    private static final BCryptVersion version = BCryptVersion.S2A;
    private static final SecureRandom random = new SecureRandom();
    private static final int STRENGTH2 = 10;
    private static final Pattern BCRYPT_PATTERN = Pattern.compile(
        "\\A\\$2([ayb])?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}"
    );

    /**
     * Encode the raw password to string hash
     *
     * @param rawPassword {@link String} raw password, not encoded
     * @return {@link String} encoded password
     */
    public String encode(@NonNull CharSequence rawPassword) {
        String salt = getSalt();
        return Bcrypt.hashpw(rawPassword.toString(), salt);
    }

    /**
     * Verify a raw string againts an encoded hashed string. Returns true if the passwords match,
     * false if they do not. The encoded string itself is never decoded.
     *
     * @param rawPassword {@link String} the raw password not encoded string
     * @param encodedPassword {@link String} the hash, encoded string to compare with
     * @return {@link Boolean} true if the match else false
     */
    public Boolean matches(
        @NonNull String rawPassword,
        @NonNull String encodedPassword
    ) {
        if (encodedPassword.length() == 0) {
            return false;
        }
        if (!EncoderService.BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
            return false;
        }
        return Bcrypt.checkpw(rawPassword, encodedPassword);
    }

    private String getSalt() {
        return Bcrypt.gensalt(
            EncoderService.version.getVersion(),
            EncoderService.STRENGTH2,
            EncoderService.random
        );
    }

    /**
     * Stores the default bcrypt version for use in configuration.
     *
     * @author Lin Feng
     */
    public enum BCryptVersion {
        S2A("$2a"),

        S2Y("$2y"),

        S2B("$2b");

        private final String version;

        BCryptVersion(String version) {
            this.version = version;
        }

        public String getVersion() {
            return this.version;
        }
    }
}
