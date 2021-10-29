export class ExperimentFields {
  public static readonly REQUEST_ID: string = "requestId";
  public static readonly EXPERIMENT_TYPE_DESCRIPTION: string = "experimentType.description";
  public static readonly EXPERIMENT_TYPE: string = "experimentType";
  public static readonly REQUEST_STATUS_DESCRIPTION: string = "requestStatus.description";
  public static readonly REQUEST_STATUS: string = "requestStatus";
  public static readonly EVALUATION_METHOD_DESCRIPTION: string = "evaluationMethod.description";
  public static readonly EVALUATION_METHOD: string = "evaluationMethod";
  public static readonly FIRST_NAME: string = "firstName";
  public static readonly EMAIL: string = "email";
  public static readonly TRAINING_DATA_PATH: string = "trainingDataAbsolutePath";
  public static readonly EXPERIMENT_PATH: string = "experimentAbsolutePath";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly START_DATE: string = "startDate";
  public static readonly END_DATE: string = "endDate";
  public static readonly DELETED_DATE: string = "deletedDate";
  public static readonly NUM_FOLDS: string = "numFolds";
  public static readonly NUM_TESTS: string = "numTests";
  public static readonly SEED: string = "seed";
  public static readonly EVALUATION_TOTAL_TIME: string = "evaluationTotalTime";
}

export class EvaluationLogFields {
  public static readonly REQUEST_ID: string = "requestId";
  public static readonly CLASSIFIER_NAME: string = "classifierInfo.classifierName";
  public static readonly REQUEST_STATUS_DESCRIPTION: string = "requestStatus.description";
  public static readonly REQUEST_STATUS: string = "requestStatus";
  public static readonly RELATION_NAME: string = "instancesInfo.relationName";
  public static readonly NUM_INSTANCES: string = "instancesInfo.numInstances";
  public static readonly NUM_ATTRIBUTES: string = "instancesInfo.numAttributes";
  public static readonly NUM_CLASSES: string = "instancesInfo.numClasses";
  public static readonly CLASS_NAME: string = "instancesInfo.className";
  public static readonly EVALUATION_METHOD_DESCRIPTION: string = "evaluationMethod.description";
  public static readonly EVALUATION_METHOD: string = "evaluationMethod";
  public static readonly NUM_FOLDS: string = "numFolds";
  public static readonly NUM_TESTS: string = "numTests";
  public static readonly SEED: string = "seed";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly START_DATE: string = "startDate";
  public static readonly END_DATE: string = "endDate";
  public static readonly EVALUATION_TOTAL_TIME: string = "evaluationTotalTime";
}

export class ClassifierOptionsRequestsFields {
  public static readonly REQUEST_ID: string = "requestId";
  public static readonly RELATION_NAME: string = "relationName";
  public static readonly CLASSIFIER_NAME: string = "classifierName";
  public static readonly EVALUATION_METHOD_DESCRIPTION: string = "evaluationMethod.description";
  public static readonly EVALUATION_METHOD: string = "evaluationMethod";
  public static readonly REQUEST_DATE: string = "requestDate";
  public static readonly RESPONSE_STATUS_DESCRIPTION: string = "responseStatus.description";
  public static readonly RESPONSE_STATUS: string = "responseStatus";
}

export class ClassifierOptionsFields {
  public static readonly ID: string = "id";
  public static readonly OPTIONS_NAME: string = "optionsName";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly CREATED_BY: string = "createdBy";
}

export class ClassifiersConfigurationFields {
  public static readonly ID: string = "id";
  public static readonly CONFIGURATION_NAME: string = "configurationName";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly CREATED_BY: string = "createdBy";
  public static readonly UPDATED: string = "updated";
  public static readonly CLASSIFIERS_OPTIONS_COUNT: string = "classifiersOptionsCount";
}

export class EvaluationStatisticsFields {
  public static readonly NUM_TEST_INSTANCES: string = "numTestInstances";
  public static readonly NUM_CORRECT: string = "numCorrect";
  public static readonly NUM_INCORRECT: string = "numIncorrect";
  public static readonly PCT_CORRECT: string = "pctCorrect";
  public static readonly PCT_INCORRECT: string = "pctIncorrect";
  public static readonly MEAN_ABSOLUTE_ERROR: string = "meanAbsoluteError";
  public static readonly ROOT_MEAN_SQUARED_ERROR: string = "rootMeanSquaredError";
  public static readonly VARIANCE_ERROR: string = "varianceError";
  public static readonly CONFIDENCE_INTERVAL: string = "confidenceInterval";
}

export class ClassificationCostsFields {
  public static readonly CLASS_VALUE: string = "classValue";
  public static readonly TPR: string = "truePositiveRate";
  public static readonly FPR: string = "falsePositiveRate";
  public static readonly TNR: string = "trueNegativeRate";
  public static readonly FNR: string = "falseNegativeRate";
  public static readonly AUC: string = "aucValue";
}

export class ExperimentResultsFields {
  public static readonly RESULTS_INDEX: string = "resultsIndex";
  public static readonly CLASSIFIER_NAME: string = "classifierInfo.classifierName";
  public static readonly PCT_CORRECT: string = "pctCorrect";
  public static readonly SENT: string = "sent";
}

export class UserFields {
  public static readonly LOGIN: string = "login";
  public static readonly EMAIL: string = "email";
  public static readonly FIRST_NAME: string = "firstName";
  public static readonly LAST_NAME: string = "lastName";
  public static readonly MIDDLE_NAME: string = "middleName";
  public static readonly FULL_NAME: string = "fullName";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly TFA_ENABLED: string = "tfaEnabled";
  public static readonly PASSWORD_DATE: string = 'passwordChangeDate';
  public static readonly ROLES: string = "roles";
}

export class InstancesFields {
  public static readonly TABLE_NAME: string = "tableName";
  public static readonly NUM_INSTANCES: string = "numInstances";
  public static readonly NUM_ATTRIBUTES: string = "numAttributes";
  public static readonly CREATED: string = "created";
  public static readonly CREATED_BY: string = "createdBy";
}

export class AuditLogFields {
  public static readonly EVENT_ID: string = "eventId";
  public static readonly EVENT_DATE: string = "eventDate";
  public static readonly GROUP_TITLE: string = "groupTitle";
  public static readonly CODE_TITLE: string = "codeTitle";
  public static readonly MESSAGE: string = "message";
  public static readonly INITIATOR: string = "initiator";
}

export class EmailTemplateFields {
  public static readonly CODE: string = "code";
  public static readonly DESCRIPTION: string = "description";
  public static readonly SUBJECT: string = "subject";
  public static readonly CREATED: string = "created";
}

export class EmailTemplateParameterFields {
  public static readonly PARAMETER_NAME: string = "parameterName";
  public static readonly DESCRIPTION: string = "description";
  public static readonly CREATED: string = "created";
}
