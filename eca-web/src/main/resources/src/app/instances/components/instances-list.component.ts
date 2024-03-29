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
import { RouterPaths } from "../../routing/router-paths";
import { Router } from "@angular/router";
import { ExportInstancesModel } from "../../export-instances/model/export-instances.model";
import { InstancesFilterFields } from "../../common/util/filter-field-names";

@Component({
  selector: 'app-instances-list',
  templateUrl: './instances-list.component.html',
  styleUrls: ['./instances-list.component.scss']
})
export class InstancesListComponent extends BaseListComponent<InstancesDto> implements OnInit {

  public createEditInstancesDialogVisibility: boolean = false;

  public createEditInstancesModel: CreateEditInstancesModel = new CreateEditInstancesModel();

  public exportInstancesDialogVisibility: boolean = false;

  public exportInstancesModel: ExportInstancesModel = new ExportInstancesModel();

  public constructor(private injector: Injector,
                     private confirmationService: ConfirmationService,
                     private instancesService: InstancesService,
                     private router: Router) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = InstancesFields.CREATED;
    this.linkColumns = [InstancesFields.RELATION_NAME];
    this.notSortableColumns = [InstancesFields.CLASS_NAME];
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
      this.createEditInstancesModel = new CreateEditInstancesModel(item.id, item.relationName);
    } else {
      this.createEditInstancesModel = new CreateEditInstancesModel();
    }
    this.createEditInstancesDialogVisibility = true;
  }

  public onRenameInstances(event): void {
    this.reloadPageWithLoader();
  }

  public onCreateInstances(createInstancesResultDto: CreateInstancesResultDto): void {
    this.messageService.add({ severity: 'success',
      summary: `Добавлен новый датасет ${createInstancesResultDto.relationName}`, detail: '' });
    this.lastCreatedId = createInstancesResultDto.id;
    this.reloadPageWithLoader();
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

  public onLink(item: InstancesDto, column: string): void {
    if (column == InstancesFields.RELATION_NAME) {
      this.router.navigate([RouterPaths.INSTANCES_DETAILS_URL, item.id]);
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${column} as link`});
    }
  }

  public onExportInstances(item: InstancesDto): void {
    this.exportInstancesModel = new ExportInstancesModel(item.id, item.relationName);
    this.exportInstancesDialogVisibility = true;
  }

  public onExportInstancesDialogVisibility(visible): void {
    this.exportInstancesDialogVisibility = visible;
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
          this.messageService.add({ severity: 'success', summary: `Данные ${item.relationName} были успешно удалены`, detail: '' });
          this.reloadPageWithLoader();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initColumns() {
    this.columns = [
      { name: InstancesFields.ID, label: "#", sortBy: InstancesFilterFields.ID },
      { name: InstancesFields.RELATION_NAME, label: "Название данных", sortBy: InstancesFilterFields.RELATION_NAME },
      { name: InstancesFields.NUM_INSTANCES, label: "Число объектов", sortBy: InstancesFilterFields.NUM_INSTANCES },
      { name: InstancesFields.NUM_ATTRIBUTES, label: "Число атрибутов", sortBy: InstancesFilterFields.NUM_ATTRIBUTES },
      { name: InstancesFields.CLASS_NAME, label: "Атрибут класса" },
      { name: InstancesFields.CREATED, label: "Дата создания", sortBy: InstancesFilterFields.CREATED },
      { name: InstancesFields.CREATED_BY, label: "Пользователь", sortBy: InstancesFilterFields.CREATED_BY },
    ];
  }
}
