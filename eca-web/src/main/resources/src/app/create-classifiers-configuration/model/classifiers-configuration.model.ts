import { OperationType } from "../../common/model/operation-type.enum";

export class ClassifiersConfigurationModel {
  operation: OperationType;
  id?: number;
  configurationName?: string;

  constructor(operation: OperationType = OperationType.CREATE, id?: number, configurationName?: string) {
    this.operation = operation;
    this.id = id;
    this.configurationName = configurationName;
  }
}
