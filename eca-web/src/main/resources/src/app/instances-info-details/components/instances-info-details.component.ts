import { Component, Injector, Input, OnInit } from '@angular/core';
import {
  InstancesInfoDetailsDto,
  AttributeMetaInfoDto,
  RoutePathDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { finalize } from "rxjs/internal/operators";
import { InstancesInfoService } from '../../common/instances-info/services/instances-info.service';
import { AttributeMetaInfoFields } from '../../common/util/field-names';
import { FieldService } from '../../common/services/field.service';
import { OverlayPanel } from 'primeng/primeng';
import { InstancesService } from '../../instances/services/instances.service';

@Component({
  selector: 'app-instances-info-details',
  templateUrl: './instances-info-details.component.html',
  styleUrls: ['./instances-info-details.component.scss']
})
export class InstancesInfoDetailsComponent implements OnInit {

  @Input()
  private id: number;

  public loading: boolean = false;
  public instancesInfoDetailsDto: InstancesInfoDetailsDto;

  public attributesTableColumns: any[] = [];

  public toggledAttribute: AttributeMetaInfoDto;

  public dataSetRoutePath: RoutePathDto;

  public constructor(private injector: Injector,
                     private instancesInfoService: InstancesInfoService,
                     private fieldService: FieldService,
                     private messageService: MessageService,
                     private instancesService: InstancesService) {
    this.initAttributesTableColumns();
  }

  public ngOnInit() {
    this.getInstancesDetails();
  }

  public getInstancesDetails(): void {
    this.loading = true;
    this.instancesInfoService.getInstancesInfoDetails(this.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (instancesInfoDetailsDto: InstancesInfoDetailsDto) => {
          this.instancesInfoDetailsDto = instancesInfoDetailsDto;
          this.getDataSetRoutePath();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getDataSetRoutePath(): void {
    this.instancesService.getInstancesPath(this.instancesInfoDetailsDto.uuid)
      .subscribe({
        next: (routePathDto: RoutePathDto) => {
          this.dataSetRoutePath = routePathDto;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public toggleOverlayPanel(event, attributeMetaInfoDto: AttributeMetaInfoDto, overlayPanel: OverlayPanel): void {
    if (attributeMetaInfoDto.type.value == 'NOMINAL') {
      this.toggledAttribute = attributeMetaInfoDto;
      overlayPanel.toggle(event);
    }
  }

  public isLink(column: string, item: AttributeMetaInfoDto): boolean {
    return column == AttributeMetaInfoFields.NAME && item.type.value == 'NOMINAL';
  }

  public getColumnValue(column: string, item: AttributeMetaInfoDto) {
    return this.fieldService.getFieldValue(column, item);
  }

  public downloadInstances(): void {
    this.loading = true;
    this.instancesInfoService.downloadInstances(this.instancesInfoDetailsDto,
      () => this.loading = false,
      (error) => this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message })
    );
  }

  public openDataSet(): void {
    window.open(this.dataSetRoutePath.path, '_blank');
  }

  private initAttributesTableColumns(): void {
    this.attributesTableColumns = [
      { name: AttributeMetaInfoFields.INDEX, label: "№" },
      { name: AttributeMetaInfoFields.NAME, label: "Атрибут" },
      { name: AttributeMetaInfoFields.TYPE_DESCRIPTION, label: "Тип" },
    ];
  }
}
