import { Component, Injector, Input, OnInit } from '@angular/core';
import {
  InstancesInfoDetailsDto,
  AttributeMetaInfoDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { ActivatedRoute, Router } from "@angular/router";
import { finalize } from "rxjs/internal/operators";
import { InstancesInfoService } from '../../common/instances-info/services/instances-info.service';
import { AttributeMetaInfoFields } from '../../common/util/field-names';
import { FieldService } from '../../common/services/field.service';
import { OverlayPanel } from 'primeng/primeng';

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

  public constructor(private injector: Injector,
                     private instancesInfoService: InstancesInfoService,
                     private fieldService: FieldService,
                     private messageService: MessageService,
                     private router: Router,
                     private route: ActivatedRoute) {
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

  private initAttributesTableColumns(): void {
    this.attributesTableColumns = [
      { name: AttributeMetaInfoFields.INDEX, label: "№" },
      { name: AttributeMetaInfoFields.NAME, label: "Атрибут" },
      { name: AttributeMetaInfoFields.TYPE_DESCRIPTION, label: "Тип" },
    ];
  }
}
