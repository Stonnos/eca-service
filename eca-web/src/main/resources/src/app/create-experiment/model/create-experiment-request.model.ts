export class CreateExperimentRequestDto {
  instancesUuid: string;
  experimentType: string;
  evaluationMethod: string;

  public constructor(instancesUuid: string, experimentType: string, evaluationMethod: string) {
    this.instancesUuid = instancesUuid;
    this.experimentType = experimentType;
    this.evaluationMethod = evaluationMethod;
  }
}
