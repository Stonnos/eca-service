package com.ecaservice.server.service.classifiers;

import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationActionType;
import com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Classifiers configuration history service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersConfigurationHistoryService {

    private final UserService userService;
    private final ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;

    /**
     * Saves created classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveCreateConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        saveToHistory(ClassifiersConfigurationActionType.CREATE_CONFIGURATION, classifiersConfiguration);
    }

    /**
     * Saves active classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveSetActiveConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        saveToHistory(ClassifiersConfigurationActionType.SET_ACTIVE, classifiersConfiguration);
    }

    /**
     * Saves added classifiers options to history.
     *
     * @param classifierOptionsDatabaseModel - classifier options entity
     */
    public void saveAddClassifierOptions(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        saveToHistory(ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS,
                classifierOptionsDatabaseModel.getConfiguration());
    }

    /**
     * Saves removed classifiers options to history.
     *
     * @param classifierOptionsDatabaseModel - classifier options entity
     */
    public void saveRemoveClassifierOptions(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        saveToHistory(ClassifiersConfigurationActionType.REMOVE_CLASSIFIER_OPTIONS,
                classifierOptionsDatabaseModel.getConfiguration());
    }

    private void saveToHistory(ClassifiersConfigurationActionType actionType,
                               ClassifiersConfiguration classifiersConfiguration) {
        log.info("Starting to save classifiers configuration [{}] action [{}] to history",
                classifiersConfiguration.getId(), actionType);
        var classifiersConfigurationHistory = new ClassifiersConfigurationHistoryEntity();
        classifiersConfigurationHistory.setActionType(actionType);
        classifiersConfigurationHistory.setMessageText(actionType.getDescription());
        classifiersConfigurationHistory.setConfiguration(classifiersConfiguration);
        classifiersConfigurationHistory.setCreatedBy(userService.getCurrentUser());
        classifiersConfigurationHistory.setCreationDate(LocalDateTime.now());
        classifiersConfigurationHistoryRepository.save(classifiersConfigurationHistory);
        log.info("Classifiers configuration [{}] action [{}] has been saved to history",
                classifiersConfiguration.getId(), actionType);
    }
}
