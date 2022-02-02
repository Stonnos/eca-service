import { Component, Injector } from '@angular/core';
import {
  AuditLogDto,
  FilterFieldDto,
  PageDto,
  PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { MessageService } from "primeng/api";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { AuditLogFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { AuditLogService } from "../services/audit-log.service";

@Component({
  selector: 'app-audit-logs',
  templateUrl: './audit-logs.component.html',
  styleUrls: ['./audit-logs.component.scss']
})
export class AuditLogsComponent extends BaseListComponent<AuditLogDto> {

  private static readonly AUDIT_LOGS_REPORT_FILE_NAME = 'audit-logs-report.xlsx';

  public constructor(private injector: Injector,
                     private auditLogService: AuditLogService,
                     private filterService: FilterService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = AuditLogFields.EVENT_DATE;
    this.initColumns();
  }

  public ngOnInit() {
    this.getFilterFields();
  }

  public getFilterFields() {
    this.filterService.getAuditLogFilterFields()
      .subscribe({
        next: (filterFields: FilterFieldDto[]) => {
          this.filters = this.filterService.mapToFilters(filterFields);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<AuditLogDto>> {
    return this.auditLogService.getAuditLogs(pageRequest);
  }

  public generateReport() {
    const observable = this.auditLogService.getAuditLogsBaseReport(this.pageRequestDto);
    this.downloadReport(observable, AuditLogsComponent.AUDIT_LOGS_REPORT_FILE_NAME);
  }

  private initColumns() {
    this.columns = [
      { name: AuditLogFields.EVENT_ID, label: "ID события" },
      { name: AuditLogFields.CORRELATION_ID, label: "ID корреляции" },
      { name: AuditLogFields.EVENT_DATE, label: "Дата события" },
      { name: AuditLogFields.GROUP_TITLE, label: "Группа событий" },
      { name: AuditLogFields.CODE_TITLE, label: "Код события" },
      { name: AuditLogFields.MESSAGE, label: "Текст сообщения" },
      { name: AuditLogFields.INITIATOR, label: "Инициатор события" },
    ];
  }
}
