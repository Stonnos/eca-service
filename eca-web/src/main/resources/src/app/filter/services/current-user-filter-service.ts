import { Filter } from "../model/filter.model";
import { UserDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { UsersService } from "../../users/services/users.service";

export class CurrentUserFilterService {

  public constructor(private userFilterField: string,
                     private usersService: UsersService,
                     private messageService: MessageService) {
  }

  public getCurrentUser(filters: Filter[]): void {
    this.usersService.getCurrentUser()
      .subscribe({
        next: (userDto: UserDto) => {
          const filter = filters.filter((item: Filter) => item.name == this.userFilterField).pop();
          filter.currentValue = {
            label: userDto.login,
            value: userDto
          };
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }
}
