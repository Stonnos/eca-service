export class ClassifiersConfigurationModel {
  id?: number;
  configurationName?: string;

  constructor(id?: number, configurationName?: string) {
    this.id = id;
    this.configurationName = configurationName;
  }
}
