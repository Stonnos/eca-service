export class CreateOptimalEvaluationRequestDto {
  instancesUuid: string;
  evaluationMethod: string;

  public constructor(instancesUuid: string, evaluationMethod: string) {
    this.instancesUuid = instancesUuid;
    this.evaluationMethod = evaluationMethod;
  }
}
