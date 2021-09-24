export class InstancesReportModel {
  label: string;
  value: string;
  fileExtension;

  public constructor(label: string, value: string, fileExtension) {
    this.label = label;
    this.value = value;
    this.fileExtension = fileExtension;
  }
}
