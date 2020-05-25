import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifierOptionsRequestDto,
  CreateExperimentResultDto,
  ExperimentDto, FilterDictionaryDto, FilterDictionaryValueDto, FilterFieldDto, PageDto,
  PageRequestDto, RequestStatusStatisticsDto, RoleDto, UserDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { saveAs } from 'file-saver/dist/FileSaver';
import { BaseListComponent } from "../../common/lists/base-list.component";
import { OverlayPanel } from "primeng/primeng";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { finalize } from "rxjs/internal/operators";
import { ExperimentRequest } from "../../create-experiment/model/experiment-request.model";
import { Router } from "@angular/router";
import { RouterPaths } from "../../routing/router-paths";
import { UserFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { UsersService } from "../services/users.service";

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss']
})
export class UsersListComponent extends BaseListComponent<UserDto> implements OnInit {

  public createUserDialogVisibility: boolean = false;

  public constructor(private injector: Injector,
                     private usersService: UsersService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = UserFields.CREATION_DATE;
    this.notSortableColumns = [UserFields.ROLES];
    this.initColumns();
  }

  public ngOnInit() {
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<UserDto>> {
    return this.usersService.getUsers(pageRequest);
  }

  public getColumnValue(column: string, item: UserDto) {
    if (column == UserFields.ROLES) {
      return item.roles && item.roles.map((role: RoleDto) => role.roleName).join(',');
    } else {
      return super.getColumnValue(column, item);
    }
  }

  /*public setPage(pageDto: PageDto<ExperimentDto>) {
    this.blinkRequestId = this.lastCreatedExperimentRequestId;
    this.lastCreatedExperimentRequestId = null;
    super.setPage(pageDto);
  }*/

  public onCreateUserDialogVisibility(visible): void {
    this.createUserDialogVisibility = visible;
  }

  /*public onCreateUser(experimentRequest: ExperimentRequest): void {
    this.loading = true;
    this.usersService.createExperiment(experimentRequest)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (result: CreateExperimentResultDto) => {
          if (result.created) {
            this.messageService.add({ severity: 'success', summary: `Эксперимент был успешно создан`, detail: '' });
            this.lastCreatedExperimentRequestId = result.requestId;
            this.getRequestStatusesStatistics();
            this.performPageRequest(0, this.pageSize, this.table.sortField, this.table.sortOrder == 1);
          } else {
            this.messageService.add({ severity: 'error', summary: 'Не удалось создать эксперимент', detail: result.errorMessage });
          }
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }*/

 // public isBlink(item: ExperimentDto): boolean {
 //   return this.blinkRequestId == item.requestId;
 // }

  public showCreateUserDialog(): void {
    this.createUserDialogVisibility = true;
  }

  private initColumns() {
    this.columns = [
      { name: UserFields.LOGIN, label: "Логин" },
      { name: UserFields.EMAIL, label: "Email пользователя" },
      { name: UserFields.FIRST_NAME, label: "Имя пользователя" },
      { name: UserFields.CREATION_DATE, label: "Дата создания" },
      { name: UserFields.ROLES, label: "Роли" }
    ];
  }
}
