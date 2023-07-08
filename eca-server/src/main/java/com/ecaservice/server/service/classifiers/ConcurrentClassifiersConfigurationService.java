package com.ecaservice.server.service.classifiers;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.report.model.ClassifiersConfigurationBean;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static com.ecaservice.core.lock.redis.config.RedisLockAutoConfiguration.REDIS_LOCK_REGISTRY;

/**
 * Classifiers configuration service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Primary
@Service("classifiersConfigurationServiceImpl")
public class ConcurrentClassifiersConfigurationService implements ClassifiersConfigurationService {

    private final ClassifiersConfigurationService classifiersConfigurationService;

    /**
     * Constructor with parameters.
     *
     * @param classifiersConfigurationService - classifiers configuration service impl
     */
    public ConcurrentClassifiersConfigurationService(@Qualifier("classifiersConfigurationServiceImpl")
                                                     ClassifiersConfigurationService classifiersConfigurationService) {
        this.classifiersConfigurationService = classifiersConfigurationService;
    }

    @Override
    public Page<ClassifiersConfiguration> getNextPage(PageRequestDto pageRequestDto) {
        return classifiersConfigurationService.getNextPage(pageRequestDto);
    }

    @Override
    public ClassifiersConfiguration save(CreateClassifiersConfigurationDto configurationDto) {
        return classifiersConfigurationService.save(configurationDto);
    }

    @Override
    public ClassifiersConfiguration update(UpdateClassifiersConfigurationDto configurationDto) {
        return classifiersConfigurationService.update(configurationDto);
    }

    @Override
    public void delete(long id) {
        classifiersConfigurationService.delete(id);
    }

    @Override
    public ClassifiersConfiguration copy(UpdateClassifiersConfigurationDto configurationDto) {
        return classifiersConfigurationService.copy(configurationDto);
    }

    @Override
    @Locked(lockName = "setActiveClassifiersConfiguration", lockRegistry = REDIS_LOCK_REGISTRY)
    public ClassifiersConfiguration setActive(long id) {
        return classifiersConfigurationService.setActive(id);
    }

    @Override
    public PageDto<ClassifiersConfigurationDto> getClassifiersConfigurations(PageRequestDto pageRequestDto) {
        return classifiersConfigurationService.getClassifiersConfigurations(pageRequestDto);
    }

    @Override
    public ClassifiersConfigurationDto getClassifiersConfigurationDetails(long id) {
        return classifiersConfigurationService.getClassifiersConfigurationDetails(id);
    }

    @Override
    public ClassifiersConfigurationBean getClassifiersConfigurationReport(long id) {
        return classifiersConfigurationService.getClassifiersConfigurationReport(id);
    }

    @Override
    public ClassifiersConfiguration getById(long id) {
        return classifiersConfigurationService.getById(id);
    }
}
