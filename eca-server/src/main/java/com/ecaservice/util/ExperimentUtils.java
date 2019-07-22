package com.ecaservice.util;

import com.ecaservice.model.entity.Experiment;
import com.google.common.base.Charsets;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

import static com.ecaservice.util.Utils.toMillis;

/**
 * Experiment utility class.
 *
 * @author Roman Batygin
 */
public class ExperimentUtils {

    private static final String SALT_FORMAT = "%s:%d";

    private ExperimentUtils() {
    }

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
     * Generate unique token for experiment by algorithm:
     * 1. Creates salt in format uuid:creation_date_millis
     * 2. Gets md5_salt = MD5(salt)
     * 3. Creates string to encode: stringToEncode = md5_salt:start_date_millis
     * 4. Gets results = base64(stringToEncode)
     *
     * @param experiment - experiment entity
     * @return experiment token
     */
    public static String generateToken(Experiment experiment) {
        String salt = String.format(SALT_FORMAT, experiment.getUuid(), toMillis(experiment.getCreationDate()));
        String md5Salt = DigestUtils.md5DigestAsHex(salt.getBytes(Charsets.UTF_8));
        String stringToEncode = String.format(SALT_FORMAT, md5Salt, toMillis(experiment.getStartDate()));
        return Base64Utils.encodeToString(stringToEncode.getBytes(Charsets.UTF_8));
    }
}
