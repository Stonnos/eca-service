export class ExperimentFields {
  public static readonly UUID: string = "uuid";
  public static readonly EXPERIMENT_TYPE: string = "experimentType";
  public static readonly EXPERIMENT_STATUS: string = "experimentStatus";
  public static readonly EVALUATION_METHOD: string = "evaluationMethod";
  public static readonly FIRST_NAME: string = "firstName";
  public static readonly EMAIL: string = "email";
  public static readonly TRAINING_DATA_PATH: string = "trainingDataAbsolutePath";
  public static readonly EXPERIMENT_PATH: string = "experimentAbsolutePath";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly START_DATE: string = "startDate";
  public static readonly END_DATE: string = "endDate";
  public static readonly SENT_DATE: string = "sentDate";
  public static readonly DELETED_DATE: string = "deletedDate";

}

export class EvaluationLogFields {
  public static readonly REQUEST_ID: string = "requestId";
  public static readonly CLASSIFIER_NAME: string = "classifierInfo.classifierName";
  public static readonly EVALUATION_STATUS: string = "evaluationStatus";
  public static readonly RELATION_NAME: string = "instancesInfo.relationName";
  public static readonly EVALUATION_METHOD: string = "evaluationMethod";
  public static readonly CREATION_DATE: string = "creationDate";
  public static readonly START_DATE: string = "startDate";
  public static readonly END_DATE: string = "endDate";
}

export class ClassifierOptionsRequestsFields {
  public static readonly REQUEST_ID: string = "requestId";
  public static readonly RELATION_NAME: string = "relationName";
  public static readonly CLASSIFIER_NAME: string = "classifierName";
  public static readonly EVALUATION_METHOD: string = "evaluationMethod";
  public static readonly REQUEST_DATE: string = "requestDate";
  public static readonly RESPONSE_STATUS: string = "responseStatus";
}

export class ClassifierOptionsFields {
  public static readonly OPTIONS_NAME: string = "optionsName";
  public static readonly VERSION: string = "version";
  public static readonly CREATION_DATE: string = "creationDate";
}
