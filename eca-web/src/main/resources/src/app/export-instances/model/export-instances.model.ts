export class ExportInstancesModel {
  reportType: string;
  fileName: string;

  public constructor(fileName?: string) {
    this.fileName = fileName;
  }
}
