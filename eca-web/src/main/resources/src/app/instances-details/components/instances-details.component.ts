import { Component, Injector } from '@angular/core';
import {
  InstancesDto,
  PageDto,
  PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { MessageService } from "primeng/api";
import { Observable } from "rxjs/internal/Observable";
import { FieldService } from "../../common/services/field.service";
import { InstancesService } from "../../instances/services/instances.service";
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: 'app-instances-details',
  templateUrl: './instances-details.component.html',
  styleUrls: ['./instances-details.component.scss']
})
export class InstancesDetailsComponent extends BaseListComponent<string[]> {

  private readonly id: number;

  private instancesDto: InstancesDto;

  public constructor(private injector: Injector,
                     private instancesService: InstancesService,
                     private route: ActivatedRoute) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.id = this.route.snapshot.params.id;
  }

  public ngOnInit() {
    this.getInstancesDetails();
    this.getAttributes();
  }

  public getInstancesDetails(): void {
    this.instancesService.getInstancesDetails(this.id)
      .subscribe({
        next: (instancesDto: InstancesDto) => {
          this.instancesDto = instancesDto;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getAttributes(): void {
    this.instancesService.getAttributes(this.id)
      .subscribe({
        next: (attributes: string[]) => {
          this.columns = attributes.map((attr: string) => { return { name: attr, label: attr} });
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<string[]>> {
    return this.instancesService.getDataPage(this.id, pageRequest);
  }

  public getColumnValue(column: string, item: string[]): any {
    const columnIndex = this.columns && this.columns.findIndex(value => value.name == column);
    return columnIndex && columnIndex >= 0 ? item[columnIndex] : null;
  }
}
