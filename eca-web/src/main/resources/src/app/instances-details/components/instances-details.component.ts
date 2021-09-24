import { Component, Injector } from '@angular/core';
import {
  InstancesDto, InstancesReportInfoDto,
  PageDto,
  PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { ConfirmationService, MessageService } from "primeng/api";
import { Observable } from "rxjs/internal/Observable";
import { FieldService } from "../../common/services/field.service";
import { InstancesService } from "../../instances/services/instances.service";
import { ActivatedRoute, Router } from "@angular/router";
import { CreateEditInstancesModel } from "../../create-edit-instances/model/create-edit-instances.model";
import { ExportInstancesModel } from "../../export-instances/model/export-instances.model";
import { InstancesReportModel } from "../../export-instances/model/instances-report.model";

@Component({
  selector: 'app-instances-details',
  templateUrl: './instances-details.component.html',
  styleUrls: ['./instances-details.component.scss']
})
export class InstancesDetailsComponent extends BaseListComponent<string[]> {

  private readonly id: number;

  private instancesDto: InstancesDto;

  public createEditInstancesDialogVisibility: boolean = false;

  public createEditInstancesModel: CreateEditInstancesModel = new CreateEditInstancesModel();

  public reportTypes: InstancesReportModel[] = [];

  public exportInstancesDialogVisibility: boolean = false;

  public exportInstancesModel: ExportInstancesModel = new ExportInstancesModel();

  public constructor(private injector: Injector,
                     private instancesService: InstancesService,
                     private confirmationService: ConfirmationService,
                     private router: Router,
                     private route: ActivatedRoute) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.id = this.route.snapshot.params.id;
  }

  public ngOnInit() {
    this.getInstancesDetails();
    this.getAttributes();
    this.getReports();
  }

  public getInstancesDetails(): void {
    this.instancesService.getInstancesDetails(this.id)
      .subscribe({
        next: (instancesDto: InstancesDto) => {
          this.instancesDto = instancesDto;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getAttributes(): void {
    this.instancesService.getAttributes(this.id)
      .subscribe({
        next: (attributes: string[]) => {
          this.columns = attributes.map((attr: string) => { return { name: attr, label: attr} });
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getReports(): void {
    this.instancesService.getInstancesReportsInfo()
      .subscribe({
        next: (reportTypes: InstancesReportInfoDto[]) => {
          this.reportTypes = reportTypes
            .map((reportType: InstancesReportInfoDto) => new InstancesReportModel(reportType.title, reportType.reportType, reportType.fileExtension));
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<string[]>> {
    return this.instancesService.getDataPage(this.id, pageRequest);
  }

  public getColumnValueByIndex(column: number, item: string[]): any {
    return item[column];
  }

  public onDeleteInstances(): void {
    this.confirmationService.confirm({
      message: 'Вы уверены?',
      acceptLabel: 'Да',
      rejectLabel: 'Нет',
      accept: () => {
        this.deleteInstances();
      }
    });
  }

  public onExportInstances(): void {
    this.exportInstancesModel = new ExportInstancesModel(this.instancesDto.tableName);
    this.exportInstancesDialogVisibility = true;
  }

  public onCreateEditInstancesDialogVisibility(visible): void {
    this.createEditInstancesDialogVisibility = visible;
  }

  public onExportInstancesDialogVisibility(visible): void {
    this.exportInstancesDialogVisibility = visible;
  }

  public renameInstances(): void {
    this.createEditInstancesModel = new CreateEditInstancesModel(this.id, this.instancesDto.tableName);
    this.createEditInstancesDialogVisibility = true;
  }

  public onRenameInstances(event): void {
    this.getInstancesDetails();
  }

  private deleteInstances(): void {
    this.instancesService.deleteInstances(this.id)
      .subscribe({
        next: () => {
          this.messageService.add({ severity: 'success',
            summary: `Данные ${this.instancesDto.tableName} были успешно удалены`, detail: '' });
          this.router.navigate(['/dashboard/instances']);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }
}
