import { Component } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { ExportInstancesModel, ReportValue } from "../model/export-instances.model";
import { finalize } from "rxjs/operators";
import { MessageService } from "primeng/api";
import { InstancesService } from "../../instances/services/instances.service";
import { saveAs } from 'file-saver/dist/FileSaver';
import { ListModel } from "../../common/model/list.model";
import { InstancesReportInfoDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-export-instances',
  templateUrl: './export-instances.component.html',
  styleUrls: ['./export-instances.component.scss']
})
export class ExportInstancesComponent extends BaseCreateDialogComponent<ExportInstancesModel> {

  public reportTypes: ListModel<ReportValue>[] = [];

  public loading: boolean = false;

  public constructor(private messageService: MessageService,
                     private instancesService: InstancesService) {
    super();
  }

  public ngOnInit(): void {
    this.getReports();
  }

  public getReports(): void {
    this.instancesService.getInstancesReportsInfo()
      .subscribe({
        next: (reportTypes: InstancesReportInfoDto[]) => {
          this.reportTypes = reportTypes
            .map((reportType: InstancesReportInfoDto) => {
              const reportValue = new ReportValue(reportType.reportType, reportType.fileExtension);
              return new ListModel<ReportValue>(reportType.title, reportValue);
            });
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public submit() {
    this.submitted = true;
    if (this.isValid()) {
      this.loading = true;
      this.instancesService.downloadInstancesReport(this.item.instancesId, this.item.reportValue.value.reportType)
        .pipe(
          finalize(() => {
            this.loading = false;
          })
        )
        .subscribe({
          next: (blob: Blob) => {
            saveAs(blob, `${this.item.fileName}.${this.item.reportValue.value.fileExtension}`);
          },
          error: (error) => {
            this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
          }
        });
    }
  }
}
