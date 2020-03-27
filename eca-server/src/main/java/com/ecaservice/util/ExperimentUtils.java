package com.ecaservice.util;

import com.ecaservice.model.entity.Experiment;
import lombok.experimental.UtilityClass;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import static com.ecaservice.util.Utils.toMillis;

/**
 * Experiment utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ExperimentUtils {

    private static final String SALT_FORMAT = "%s:%d";

    /**
     * Gets experiment file.
     *
     * @param experiment       - experiment entity
     * @param filePathFunction - file path predicate
     * @return file object
     */
    public static File getExperimentFile(Experiment experiment, Function<Experiment, String> filePathFunction) {
        return Optional.ofNullable(experiment).map(filePathFunction).map(File::new).orElse(null);
    }

    /**
     * Generates unique token for experiment by algorithm:
     * 1. Creates salt in format experiment_request_id:experiment_creation_date_millis
     * 2. Gets md5_salt = MD5(salt)
     * 3. Creates string in format md5_salt:token_creation_date_millis
     * 4. Gets results = base64(md5_salt:token_creation_date_millis)
     *
     * @param experiment - experiment entity
     * @return experiment token
     */
    public static String generateToken(Experiment experiment) {
        LocalDateTime tokenCreationDate = LocalDateTime.now();
        String salt = String.format(SALT_FORMAT, experiment.getRequestId(), toMillis(experiment.getCreationDate()));
        String md5Salt = DigestUtils.md5DigestAsHex(salt.getBytes(StandardCharsets.UTF_8));
        String stringToEncode = String.format(SALT_FORMAT, md5Salt, toMillis(tokenCreationDate));
        return Base64Utils.encodeToString(stringToEncode.getBytes(StandardCharsets.UTF_8));
    }
}
