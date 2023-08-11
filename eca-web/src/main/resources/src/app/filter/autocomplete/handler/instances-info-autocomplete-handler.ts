import { AutocompleteHandler } from "./autocomplete-handler";
import { InstancesInfoService } from "../../../common/instances-info/services/instances-info.service";
import { Filter } from "../../model/filter.model";
import {
  InstancesInfoDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { InstancesInfoDtoFields } from "../../../common/util/field-names";
import { AutocompleteItemModel}  from "../../model/autocomplete-item.model";
import { MessageService } from "primeng/api";

export class InstancesInfoAutocompleteHandler extends AutocompleteHandler {

  private pageSize: number = 100;

  public constructor(private instancesInfoService: InstancesInfoService,
                     private messageService: MessageService) {
    super('instancesInfo.id');
  }

  public onFilterFieldAutocomplete(filters: Filter[], autocompleteItemModel: AutocompleteItemModel): void {
    const pageRequest: PageRequestDto = {
      page: 0,
      size: this.pageSize,
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
    this.getInstancesInfoFilterValues(filters, autocompleteItemModel.filterField, pageRequest);
  }

  private getInstancesInfoFilterValues(filters: Filter[], filterField: string, pageRequest: PageRequestDto): void {
    this.instancesInfoService.getInstancesInfoPage(pageRequest)
      .subscribe({
        next: (instancesInfoPage: PageDto<InstancesInfoDto>) => {
          const filter = filters.filter((item: Filter) => item.name == filterField).pop();
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
}
