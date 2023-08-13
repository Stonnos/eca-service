export class ExperimentFilterFields {
  public static readonly REQUEST_ID: string = "requestId";
  public static readonly EXPERIMENT_TYPE: string = "experimentType";
  public static readonly REQUEST_STATUS: string = "requestStatus";
  public static readonly EVALUATION_METHOD: string = "evaluationMethod";
  public static readonly CREATED_BY: string = "createdBy";
  public static readonly MODEL_PATH: string = "modelPath";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly START_DATE: string = "startDate";
  public static readonly END_DATE: string = "endDate";
  public static readonly DELETED_DATE: string = "deletedDate";
  public static readonly MAX_PCT_CORRECT: string = "maxPctCorrect";
  public static readonly RELATION_NAME: string = "instancesInfo.relationName";
}

export class EvaluationLogFilterFields {
  public static readonly REQUEST_ID: string = "requestId";
  public static readonly CLASSIFIER_NAME: string = "classifierInfo.classifierName";
  public static readonly REQUEST_STATUS: string = "requestStatus";
  public static readonly RELATION_NAME: string = "instancesInfo.relationName";
  public static readonly EVALUATION_METHOD: string = "evaluationMethod";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly START_DATE: string = "startDate";
  public static readonly END_DATE: string = "endDate";
  public static readonly MODEL_PATH: string = "modelPath";
  public static readonly DELETED_DATE: string = "deletedDate";
  public static readonly PCT_CORRECT: string = "pctCorrect";
}

export class ClassifierOptionsRequestsFilterFields {
  public static readonly REQUEST_ID: string = "requestId";
  public static readonly RELATION_NAME: string = "instancesInfo.relationName";
  public static readonly EVALUATION_METHOD: string = "evaluationMethod";
  public static readonly REQUEST_DATE: string = "requestDate";
  public static readonly RESPONSE_STATUS: string = "responseStatus";
}

export class ClassifiersConfigurationFilterFields {
  public static readonly CONFIGURATION_NAME: string = "configurationName";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly CREATED_BY: string = "createdBy";
  public static readonly UPDATED: string = "updated";
}

export class AuditLogFilterFields {
  public static readonly EVENT_ID: string = "eventId";
  public static readonly CORRELATION_ID: string = "correlationId";
  public static readonly EVENT_DATE: string = "eventDate";
  public static readonly GROUP_TITLE: string = "groupTitle";
  public static readonly CODE_TITLE: string = "codeTitle";
  public static readonly MESSAGE: string = "message";
  public static readonly INITIATOR: string = "initiator";
}


export class ClassifiersConfigurationHistoryFilterFields {
  public static readonly ACTION_TYPE: string = "actionType";
  public static readonly CREATED_BY: string = "createdBy";
  public static readonly CREATED_AT: string = "createdAt";
  public static readonly MESSAGE_TEXT: string = "messageText";
}

export class EmailTemplateFilterFields {
  public static readonly CODE: string = "code";
  public static readonly DESCRIPTION: string = "description";
  public static readonly SUBJECT: string = "subject";
  public static readonly CREATED: string = "created";
}

export class UserFilterFields {
  public static readonly LOGIN: string = "login";
  public static readonly EMAIL: string = "email";
  public static readonly FULL_NAME: string = "fullName";
  public static readonly CREATION_DATE: string = "creationDate";
}

export class InstancesInfoFilterFields {
  public static readonly RELATION_NAME: string = "relationName";
  public static readonly CREATED_DATE: string = "createdDate";
}

export class ClassifierOptionsFilterFields {
  public static readonly ID: string = "id";
  public static readonly OPTIONS_NAME: string = "optionsName";
  public static readonly OPTIONS_DESCRIPTION: string = "optionsDescription";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly CREATED_BY: string = "createdBy";
}
