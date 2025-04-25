package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProgressEntity;
import com.ecaservice.server.model.experiment.ExperimentProgressData;
import com.ecaservice.web.dto.model.ExperimentProgressDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Experiment progress mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentProgressMapper {

    private static final String GMT_TIME_ZONE = "GMT";
    private static final int FULL_PROGRESS = 100;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Maps experiment progress entity to dto model.
     *
     * @param experimentProgressEntity - experiment progress entity
     * @return experiment progress dto
     */
    public abstract ExperimentProgressDto map(ExperimentProgressEntity experimentProgressEntity);

    /**
     * Maps experiment progress entity to data model
     *
     * @param experimentProgressEntity - experiment progress entity
     * @return experiment progress data
     */
    @Mapping(source = "experiment.id", target = "experimentId")
    public abstract ExperimentProgressData mapToExperimentData(ExperimentProgressEntity experimentProgressEntity);

    @AfterMapping
    protected void mapEstimatedTimeLeft(ExperimentProgressEntity experimentProgressEntity,
                                        @MappingTarget ExperimentProgressDto experimentProgressDto) {
        Experiment experiment = experimentProgressEntity.getExperiment();
        if (!experimentProgressEntity.isFinished() && experimentProgressEntity.getProgress() > 0) {
            long totalTimeMillis =
                    ChronoUnit.MILLIS.between(experiment.getStartDate(), LocalDateTime.now());
            int progressLeft = FULL_PROGRESS - experimentProgressEntity.getProgress();
            long timeLeftMillis = progressLeft * totalTimeMillis / experimentProgressEntity.getProgress();
            LocalDateTime timeLeft =
                    Instant.ofEpochMilli(timeLeftMillis).atZone(ZoneId.of(GMT_TIME_ZONE)).toLocalDateTime();
            experimentProgressDto.setEstimatedTimeLeft(timeFormatter.format(timeLeft));
        }
    }
}
