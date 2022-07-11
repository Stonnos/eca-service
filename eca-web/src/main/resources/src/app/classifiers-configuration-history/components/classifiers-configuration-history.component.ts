import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifiersConfigurationHistoryDto,
  FilterFieldDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";
import { ClassifiersConfigurationHistoryFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { ActivatedRoute } from "@angular/router";
import { ClassifiersConfigurationsService } from "../../classifiers-configurations/services/classifiers-configurations.service";
import { FilterService } from "../../filter/services/filter.service";

@Component({
  selector: 'app-classifiers-configuration-history',
  templateUrl: './classifiers-configuration-history.component.html',
  styleUrls: ['./classifiers-configuration-history.component.scss']
})
export class ClassifiersConfigurationHistoryComponent extends BaseListComponent<ClassifiersConfigurationHistoryDto> implements OnInit {

  private readonly configurationId: number;

  public constructor(private injector: Injector,
                     private classifiersConfigurationService: ClassifiersConfigurationsService,
                     private filterService: FilterService,
                     private route: ActivatedRoute) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.configurationId = this.route.snapshot.params.id;
    this.defaultSortField = ClassifiersConfigurationHistoryFields.CREATED_AT;
    this.initColumns();
  }

  public ngOnInit() {
    this.getFilterFields();
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ClassifiersConfigurationHistoryDto>> {
    return this.classifiersConfigurationService.getClassifiersConfigurationHistory(this.configurationId, pageRequest);
  }

  public getFilterFields() {
    this.filterService.getClassifiersConfigurationHistoryFilterFields()
      .subscribe({
        next: (filterFields: FilterFieldDto[]) => {
          this.filters = this.filterService.mapToFilters(filterFields);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initColumns() {
    this.columns = [
      { name: ClassifiersConfigurationHistoryFields.ACTION_TYPE_DESCRIPTION, label: "Тип события", sortBy: ClassifiersConfigurationHistoryFields.ACTION_TYPE },
      { name: ClassifiersConfigurationHistoryFields.CREATED_BY, label: "Пользователь" },
      { name: ClassifiersConfigurationHistoryFields.CREATED_AT, label: "Дата события" },
      { name: ClassifiersConfigurationHistoryFields.MESSAGE_TEXT, label: "Текст сообщения" },
    ];
  }
}
