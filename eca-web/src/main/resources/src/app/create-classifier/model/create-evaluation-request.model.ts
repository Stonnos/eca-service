export class CreateEvaluationRequestDto {
  instancesUuid: string;
  classifierOptions: any
  evaluationMethod: string;

  public constructor(instancesUuid: string, classifierOptions: any, evaluationMethod: string) {
    this.instancesUuid = instancesUuid;
    this.classifierOptions = classifierOptions;
    this.evaluationMethod = evaluationMethod;
  }
}
