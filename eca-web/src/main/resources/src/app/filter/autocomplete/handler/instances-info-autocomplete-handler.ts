import { AutocompleteHandler } from "./autocomplete-handler";
import { Filter } from "../../model/filter.model";
import {
  InstancesInfoDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { AutocompleteItemModel}  from "../../model/autocomplete-item.model";
import { MessageService } from "primeng/api";
import { InstancesInfoFilterFields } from "../../../common/util/filter-field-names";
import { InstancesInfoPageService } from "../../../common/services/instances-info-page.service";

export class InstancesInfoAutocompleteHandler extends AutocompleteHandler {

  private pageSize: number = 100;

  public constructor(private instancesInfoPageService: InstancesInfoPageService,
                     private messageService: MessageService) {
    super('instancesInfo.id');
  }

  public onFilterFieldAutocomplete(filters: Filter[], autocompleteItemModel: AutocompleteItemModel): void {
    const pageRequest: PageRequestDto = {
      page: 0,
      size: this.pageSize,
      sortFields: [
        {
          sortField: InstancesInfoFilterFields.CREATED_DATE,
          ascending: false,
        }
      ],
      searchQuery: null,
      filters: [
        {
          name: InstancesInfoFilterFields.RELATION_NAME,
          values: [autocompleteItemModel.searchQuery],
          matchMode: 'LIKE'
        }
      ]
    };
    this.getInstancesInfoFilterValues(filters, autocompleteItemModel.filterField, pageRequest);
  }

  private getInstancesInfoFilterValues(filters: Filter[], filterField: string, pageRequest: PageRequestDto): void {
    this.instancesInfoPageService.getInstancesInfoPage(pageRequest)
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
