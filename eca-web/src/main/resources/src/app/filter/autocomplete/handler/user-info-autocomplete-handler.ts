import { AutocompleteHandler } from "./autocomplete-handler";
import { Filter } from "../../model/filter.model";
import {
  PageDto,
  PageRequestDto, UserDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { UserFields } from "../../../common/util/field-names";
import { AutocompleteItemModel}  from "../../model/autocomplete-item.model";
import { MessageService } from "primeng/api";
import { UsersService } from "../../../users/services/users.service";

export class UserInfoAutocompleteHandler extends AutocompleteHandler {

  private pageSize: number = 100;

  public constructor(filterField: string,
                     private usersService: UsersService,
                     private messageService: MessageService) {
    super(filterField);
  }

  public onFilterFieldAutocomplete(filters: Filter[], autocompleteItemModel: AutocompleteItemModel): void {
    const pageRequest: PageRequestDto = {
      page: 0,
      size: this.pageSize,
      sortFields: [
        {
          sortField: UserFields.LOGIN,
          ascending: false,
        }
      ],
      searchQuery: null,
      filters: [
        {
          name: UserFields.LOGIN,
          values: [autocompleteItemModel.searchQuery],
          matchMode: 'LIKE'
        }
      ]
    };
    this.getUsersFilterValues(filters, autocompleteItemModel.filterField, pageRequest);
  }

  private getUsersFilterValues(filters: Filter[], filterField: string, pageRequest: PageRequestDto): void {
    this.usersService.getUsers(pageRequest)
      .subscribe({
        next: (userInfoPage: PageDto<UserDto>) => {
          const filter = filters.filter((item: Filter) => item.name == filterField).pop();
          filter.values = userInfoPage.content.map((item: UserDto) => {
            return {
              label: item.login,
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
