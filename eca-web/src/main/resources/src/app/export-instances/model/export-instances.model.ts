import { ListModel } from "../../common/model/list.model";

export class ExportInstancesModel {
  instancesId: number;
  reportValue: ListModel<ReportValue>;
  fileName: string;

  public constructor(instancesId?: number, fileName?: string) {
    this.instancesId = instancesId;
    this.fileName = fileName;
  }
}

export class ReportValue {
  reportType: string;
  fileExtension: string;

  public constructor(reportType: string, fileExtension: string) {
    this.reportType = reportType;
    this.fileExtension = fileExtension;
  }
}
