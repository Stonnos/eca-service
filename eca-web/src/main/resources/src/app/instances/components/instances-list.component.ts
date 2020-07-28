import { Component, Injector, OnInit } from '@angular/core';
import {
  CreateInstancesResultDto,
  InstancesDto,
  PageDto,
  PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ConfirmationService, MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";
import { InstancesFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { InstancesService } from "../services/instances.service";
import { finalize } from "rxjs/internal/operators";
import { CreateEditInstancesModel } from "../../create-edit-instances/model/create-edit-instances.model";

@Component({
  selector: 'app-instances-list',
  templateUrl: './instances-list.component.html',
  styleUrls: ['./instances-list.component.scss']
})
export class InstancesListComponent extends BaseListComponent<InstancesDto> implements OnInit {

  public createEditInstancesDialogVisibility: boolean = false;

  public previousInstancesModel: CreateEditInstancesModel;
  public createEditInstancesModel: CreateEditInstancesModel = new CreateEditInstancesModel();

  public constructor(private injector: Injector,
                     private confirmationService: ConfirmationService,
                     private instancesService: InstancesService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = InstancesFields.CREATED;
    this.initColumns();
  }

  public ngOnInit() {
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<InstancesDto>> {
    return this.instancesService.getInstancesPage(pageRequest);
  }

  public onCreateEditInstancesDialogVisibility(visible): void {
    this.createEditInstancesDialogVisibility = visible;
  }

  public showCreateEditInstancesDialog(item?: InstancesDto): void {
    if (item && item.id) {
      this.previousInstancesModel = new CreateEditInstancesModel(item.id, item.tableName);
      this.createEditInstancesModel = new CreateEditInstancesModel(item.id, item.tableName);
    } else {
      this.createEditInstancesModel = new CreateEditInstancesModel();
    }
    this.createEditInstancesDialogVisibility = true;
  }

  public onRenameInstances(event): void {
    this.refreshInstancesPage();
  }

  public onCreateInstances(createInstancesResultDto: CreateInstancesResultDto): void {
    this.messageService.add({ severity: 'success', summary: `Добавлен новый датасет ${createInstancesResultDto.tableName}`, detail: '' });
    this.lastCreatedId = createInstancesResultDto.id;
    this.refreshInstancesPage();
  }

  private refreshInstancesPage(): void {
    this.performPageRequest(0, this.pageSize, this.table.sortField, this.table.sortOrder == 1);
  }

  public onDeleteInstances(item: InstancesDto): void {
    this.confirmationService.confirm({
      message: 'Вы уверены?',
      acceptLabel: 'Да',
      rejectLabel: 'Нет',
      accept: () => {
        this.deleteInstances(item);
      }
    });
  }

  private deleteInstances(item: InstancesDto): void {
    this.loading = true;
    this.instancesService.deleteInstances(item.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: `Данные ${item.tableName} были успешно удалены`, detail: '' });
          this.refreshInstancesPage();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initColumns() {
    this.columns = [
      { name: InstancesFields.TABLE_NAME, label: "Название таблицы" },
      { name: InstancesFields.NUM_INSTANCES, label: "Число объектов" },
      { name: InstancesFields.NUM_ATTRIBUTES, label: "Число атрибутов" },
      { name: InstancesFields.CREATED, label: "Дата создания" },
      { name: InstancesFields.CREATED_BY, label: "Пользователь" },
    ];
  }
}
