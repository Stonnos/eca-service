import { FormField } from "../../form-templates/model/form-template.model";

export class EvaluationRequest {
  instancesUuid: string;
  evaluationMethod: any;
  classifierOptions: FormField[] = [];
}
