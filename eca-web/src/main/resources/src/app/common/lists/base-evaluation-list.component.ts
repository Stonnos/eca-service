import { BaseListComponent } from "./base-list.component";
import {
  InstancesInfoDto, PageDto, PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Injector } from "@angular/core";
import { InstancesInfoService } from "../instances-info/services/instances-info.service";
import { MessageService } from "primeng/api";
import { FieldService } from "../services/field.service";
import { AutocompleteItemModel } from "../../filter/model/autocomplete-item.model";
import { InstancesInfoDtoFields } from "../util/field-names";
import { Filter} from "../../filter/model/filter.model";


export abstract class BaseEvaluationListComponent<T> extends BaseListComponent<T> {

  private instancesInfoFilterField: string = 'instancesInfo.id';
  private instancesPageSize: number = 100;

  protected constructor(injector: Injector,
                        private instancesInfoService: InstancesInfoService) {
    super(injector.get(MessageService), injector.get(FieldService));
  }

  public onFilterFieldAutocomplete(autocompleteItemModel: AutocompleteItemModel): void {
    if (autocompleteItemModel.filterField == this.instancesInfoFilterField) {
      const pageRequest: PageRequestDto = {
        page: 0,
        size: this.instancesPageSize,
        sortField: InstancesInfoDtoFields.CREATED_DATE,
        ascending: false,
        searchQuery: null,
        filters: [
          {
            name: InstancesInfoDtoFields.RELATION_NAME,
            values: [autocompleteItemModel.searchQuery],
            matchMode: 'LIKE'
          }
        ]
      };
      this.getInstancesInfoFilterValues(autocompleteItemModel.filterField, pageRequest);
    }
  }

  private getInstancesInfoFilterValues(filterField: string, pageRequest: PageRequestDto): void {
    this.instancesInfoService.getInstancesInfoPage(pageRequest)
      .subscribe({
        next: (instancesInfoPage: PageDto<InstancesInfoDto>) => {
          const filter = this.filters.filter((item: Filter) => item.name == filterField).pop();
          filter.values = instancesInfoPage.content.map((item: InstancesInfoDto) => {
            return {
              label: item.relationName,
              value: item
            }
          });
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  protected tramsFormLazyReferenceValue(filter: Filter, values: any[]): string[] {
    if (filter.name == this.instancesInfoFilterField) {
      return values.map((item) => item.value.id);
    }
    return super.tramsFormLazyReferenceValue(filter, values);
  }
}
