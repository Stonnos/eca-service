package com.ecaservice.service;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.model.entity.AppInstanceEntity;
import com.ecaservice.repository.AppInstanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


/**
 * Unit tests for {@link AppInstanceService} functionality.
 *
 * @author Roman Batygin
 */
@Import(CommonConfig.class)
class AppInstanceServiceTest extends AbstractJpaTest {

    @Inject
    private CommonConfig commonConfig;
    @Inject
    private AppInstanceRepository appInstanceRepository;

    private AppInstanceService appInstanceService;

    @Override
    public void init() {
        appInstanceService = new AppInstanceService(commonConfig, appInstanceRepository);
    }

    @Override
    public void deleteAll() {
        appInstanceRepository.deleteAll();
    }

    @Test
    void testSaveAppInstance() {
        appInstanceService.saveAppInstance();
        assertThat(appInstanceRepository.count()).isOne();
        AppInstanceEntity appInstanceEntity = appInstanceService.getAppInstanceEntity();
        assertThat(appInstanceEntity).isNotNull();
        assertThat(appInstanceEntity.getInstanceName()).isEqualTo(commonConfig.getInstance());
    }
}
