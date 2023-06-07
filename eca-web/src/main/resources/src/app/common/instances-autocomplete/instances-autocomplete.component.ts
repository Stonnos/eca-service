import { Component, Input, OnInit } from '@angular/core';
import {
  InstancesDto, PageDto, PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { InstancesService } from "../../instances/services/instances.service";
import { MessageService } from "primeng/components/common/messageservice";
import { InstancesFields } from "../util/field-names";

@Component({
  selector: 'app-instances-autocomplete',
  templateUrl: './instances-autocomplete.component.html',
  styleUrls: ['./instances-autocomplete.component.scss']
})
export class InstancesAutocompleteComponent implements OnInit {

  private total: number = 0;
  private pageSize: number = 100;

  @Input()
  public submitted: boolean = false;

  public instances: InstancesDto[] = [];

  public selectedInstances: InstancesDto;

  public constructor(private instancesService: InstancesService,
                     private messageService: MessageService) {
  }

  public ngOnInit() {
  }

  public onComplete(event): void {
    this.getNextPage(event.query);
  }


  private getNextPage(query: string): void {
    const pageRequest: PageRequestDto = {
      page: 0,
      size: this.pageSize,
      sortField: InstancesFields.CREATED,
      ascending: false,
      searchQuery: null,
      filters: [
        {
          name: InstancesFields.TABLE_NAME,
          values: [query],
          matchMode: 'LIKE'
        }
      ]
    };
    this.getInstances(pageRequest);
  }

  private getInstances(pageRequest: PageRequestDto): void {
    this.instancesService.getInstancesPage(pageRequest)
      .subscribe({
        next: (pageDto: PageDto<InstancesDto>) => {
          this.total = pageDto.totalCount;
          this.instances = pageDto.content;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public clear(): void {
    this.instances = [];
    this.selectedInstances = null;
  }
}
