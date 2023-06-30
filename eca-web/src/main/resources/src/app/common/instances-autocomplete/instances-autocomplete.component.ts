import { Component, Input, OnInit,  EventEmitter, Output } from '@angular/core';
import {
  InstancesDto, PageDto, PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { InstancesService } from "../../instances/services/instances.service";
import { MessageService } from "primeng/components/common/messageservice";
import { InstancesFields } from "../util/field-names";
import { FieldService } from "../services/field.service";
import { Utils } from "../util/utils";

@Component({
  selector: 'app-instances-autocomplete',
  templateUrl: './instances-autocomplete.component.html',
  styleUrls: ['./instances-autocomplete.component.scss']
})
export class InstancesAutocompleteComponent implements OnInit {

  public fields: any[] = [];

  public autocompleted: boolean = false;

  public pageSize: number = 100;

  @Input()
  public submitted: boolean = false;

  public instances: InstancesDto[] = [];

  public selectedInstances: InstancesDto;

  @Output()
  public onInstancesSelected: EventEmitter<InstancesDto> = new EventEmitter<InstancesDto>();
  @Output()
  public onInstancesUnselected: EventEmitter<any> = new EventEmitter<any>();

  public constructor(private instancesService: InstancesService,
                     private fieldService: FieldService,
                     private messageService: MessageService) {
  }

  public ngOnInit() {
    this.fields = [
      { name: InstancesFields.NUM_INSTANCES, label: "Число объектов:" },
      { name: InstancesFields.NUM_ATTRIBUTES, label: "Число атрибутов:" },
      { name: InstancesFields.CLASS_NAME, label: "Атрибут класса:" },
    ];
  }

  public getFieldValue(field: string, item: InstancesDto) {
    return this.fieldService.getFieldValue(field, item, Utils.MISSING_VALUE);
  }

  public onComplete(event): void {
    this.autocompleted = true;
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
    this.autocompleted = false;
  }

  public onSelect(): void {
    this.autocompleted = false;
    this.onInstancesSelected.emit(this.selectedInstances);
  }

  public onUnselect(): void {
    this.autocompleted = false;
    this.onInstancesUnselected.emit();
  }
}
