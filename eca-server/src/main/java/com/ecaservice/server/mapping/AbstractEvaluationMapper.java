package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.model.entity.InstancesInfo;
import org.mapstruct.Named;
import org.springframework.util.DigestUtils;
import weka.core.Instances;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.ecaservice.server.util.InstancesUtils.toJson;

/**
 * Abstract evaluation mapper.
 *
 * @author Roman Batygin
 */
public abstract class AbstractEvaluationMapper {

    private static final String GMT_TIME_ZONE = "GMT";

    private final DateTimeFormatter evaluationTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");

    @Named("calculateEvaluationTotalTime")
    protected String calculateEvaluationTotalTime(AbstractEvaluationEntity evaluationEntity) {
        if (evaluationEntity.getStartDate() != null && evaluationEntity.getEndDate() != null) {
            long totalTimeMillis =
                    ChronoUnit.MILLIS.between(evaluationEntity.getStartDate(), evaluationEntity.getEndDate());
            LocalDateTime totalTime =
                    Instant.ofEpochMilli(totalTimeMillis).atZone(ZoneId.of(GMT_TIME_ZONE)).toLocalDateTime();
            return evaluationTimeFormatter.format(totalTime);
        }
        return null;
    }

    @Named("mapInstancesInfo")
    protected InstancesInfo mapInstancesInfo(Instances data) {
        if (data != null) {
            InstancesInfo instancesInfo = new InstancesInfo();
            instancesInfo.setRelationName(data.relationName());
            instancesInfo.setNumInstances(data.numInstances());
            instancesInfo.setNumAttributes(data.numAttributes());
            instancesInfo.setNumClasses(data.numClasses());
            instancesInfo.setClassName(data.classAttribute().name());
            String jsonData = toJson(data);
            String dataMd5Hash = DigestUtils.md5DigestAsHex(jsonData.getBytes(StandardCharsets.UTF_8));
            instancesInfo.setDataMd5Hash(dataMd5Hash);
            return instancesInfo;
        }
        return null;
    }
}
