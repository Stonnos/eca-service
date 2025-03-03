package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.exception.InvalidClassIndexException;
import com.ecaservice.server.exception.ModelDeletedException;
import com.ecaservice.server.exception.UnexpectedRequestStatusException;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.ModelCacheLoader;
import com.ecaservice.server.service.ModelProvider;
import com.ecaservice.web.dto.model.RocCurveDataDto;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.buildExperiment;
import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static com.ecaservice.server.TestHelperUtils.createExperimentResultsEntity;
import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExperimentResultsRocCurveDataProvider} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentResultsRocCurveDataProvider.class, ModelProvider.class, ModelCacheLoader.class, AppProperties.class})
class ExperimentResultsRocCurveDataProviderTest extends AbstractJpaTest {

    private static final int SCALE = 4;
    private static final int CLASS_INDEX = 0;

    @Autowired
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;
    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private ExperimentResultsRocCurveDataProvider experimentResultsRocCurveDataProvider;

    @MockBean
    private ObjectStorageService objectStorageService;

    private Experiment experiment;
    private ExperimentResultsEntity experimentResultsEntity;

    @Override
    public void deleteAll() {
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Override
    public void init() {
        InstancesInfo instancesInfo = createInstancesInfo();
        instancesInfoRepository.save(instancesInfo);
        experiment = createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        experimentRepository.save(experiment);
        experimentResultsEntity = createExperimentResultsEntity(experiment);
        experimentResultsEntityRepository.save(experimentResultsEntity);
    }

    @Test
    void testCalculateRocCurveData() throws IOException, ClassNotFoundException {
        AbstractExperiment<?> abstractExperiment = buildExperiment();
        EvaluationResults evaluationResults = abstractExperiment.getHistory().getFirst();
        when(objectStorageService.getObject(experimentResultsEntity.getExperiment().getModelPath(), Object.class))
                .thenReturn(abstractExperiment);
        RocCurveDataDto rocCurveDataDto =
                experimentResultsRocCurveDataProvider.getRocCurveData(experimentResultsEntity.getId(), CLASS_INDEX);
        assertThat(rocCurveDataDto).isNotNull();
        BigDecimal expectedAucValue = BigDecimal.valueOf(evaluationResults.getEvaluation().areaUnderROC(CLASS_INDEX))
                .setScale(SCALE, RoundingMode.HALF_UP);
        assertThat(rocCurveDataDto.getAucValue()).isEqualTo(expectedAucValue);
        assertThat(rocCurveDataDto.getRocCurvePoints()).isNotEmpty();
    }

    @Test
    void testCalculateRocCurveDataWithEntityNotFoundError() {
        assertThrows(EntityNotFoundException.class,
                () -> experimentResultsRocCurveDataProvider.getRocCurveData(-1L, CLASS_INDEX));
    }

    @Test
    void testCalculateRocCurveDataWithInvalidNegativeClassIndex() {
        assertThrows(InvalidClassIndexException.class,
                () -> experimentResultsRocCurveDataProvider.getRocCurveData(experimentResultsEntity.getId(), -1));
    }

    @Test
    void testCalculateRocCurveDataWithInvalidClassIndex() {
        assertThrows(InvalidClassIndexException.class,
                () -> experimentResultsRocCurveDataProvider.getRocCurveData(experimentResultsEntity.getId(),
                        experimentResultsEntity.getExperiment().getInstancesInfo().getNumClasses()));
    }

    @Test
    void testCalculateRocCurveDataWithInvalidRequestStatus() {
        experiment.setRequestStatus(RequestStatus.ERROR);
        experimentRepository.save(experiment);
        assertThrows(UnexpectedRequestStatusException.class,
                () -> experimentResultsRocCurveDataProvider.getRocCurveData(experimentResultsEntity.getId(),
                        CLASS_INDEX));
    }

    @Test
    void testCalculateRocCurveDataWithDeletedModel() {
        experiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        assertThrows(ModelDeletedException.class,
                () -> experimentResultsRocCurveDataProvider.getRocCurveData(experimentResultsEntity.getId(),
                        CLASS_INDEX));
    }
}
