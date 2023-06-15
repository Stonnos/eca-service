import { Component, Injector, ViewChild } from '@angular/core';
import {
  AttributeDto,
  InstancesDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { ConfirmationService, MessageService } from "primeng/api";
import { Observable } from "rxjs/internal/Observable";
import { FieldService } from "../../common/services/field.service";
import { InstancesService } from "../../instances/services/instances.service";
import { ActivatedRoute, Router } from "@angular/router";
import { CreateEditInstancesModel } from "../../create-edit-instances/model/create-edit-instances.model";
import { ExportInstancesModel } from "../../export-instances/model/export-instances.model";
import { EditAttributeModel } from "../../attributes/model/edit-attribute.model";
import { finalize } from "rxjs/internal/operators";
import { ValidationErrorCode } from "../../common/model/validation-error-code";
import { ErrorHandler } from "../../common/services/error-handler";
import { AttributesComponent } from "../../attributes/components/attributes.component";

@Component({
  selector: 'app-instances-details',
  templateUrl: './instances-details.component.html',
  styleUrls: ['./instances-details.component.scss']
})
export class InstancesDetailsComponent extends BaseListComponent<string[]> {

  private readonly id: number;

  private readonly errorCodes: string[] = [
    ValidationErrorCode.INVALID_CLASS_ATTRIBUTE_TYPE,
    ValidationErrorCode.CLASS_VALUES_IS_TOO_LOW
  ];

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.INVALID_CLASS_ATTRIBUTE_TYPE, 'Атрибут класса должен иметь категориальный тип')
    .set(ValidationErrorCode.CLASS_VALUES_IS_TOO_LOW, 'Число классов должно быть не менее двух');

  @ViewChild(AttributesComponent, { static: true })
  private attributesComponent: AttributesComponent;

  public instancesDto: InstancesDto;

  public attributedLoading: boolean = false;
  public attributes: AttributeDto[] = [];
  public classAttribute: AttributeDto;

  public createEditInstancesDialogVisibility: boolean = false;

  public createEditInstancesModel: CreateEditInstancesModel = new CreateEditInstancesModel();

  public exportInstancesDialogVisibility: boolean = false;

  public exportInstancesModel: ExportInstancesModel = new ExportInstancesModel();

  public constructor(private injector: Injector,
                     private instancesService: InstancesService,
                     private confirmationService: ConfirmationService,
                     private errorHandler: ErrorHandler,
                     private router: Router,
                     private route: ActivatedRoute) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.id = this.route.snapshot.params.id;
  }

  public ngOnInit() {
    this.getInstancesDetails();
  }

  public getInstancesDetails(): void {
    this.instancesService.getInstancesDetails(this.id)
      .subscribe({
        next: (instancesDto: InstancesDto) => {
          this.instancesDto = instancesDto;
          this.getAttributes();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public onSelectAttribute(item: EditAttributeModel): void {
    if (item.selected) {
      this.selectAttribute(item.id);
    } else {
      this.unselectAttribute(item.id);
    }
  }

  public onSelectAll(): void {
    this.attributedLoading = true;
    this.instancesService.selectAllAttributes(this.id)
      .pipe(
        finalize(() => {
          this.attributedLoading = false;
        })
      )
      .subscribe({
        next: () => {
          this.getAttributes();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public onSetClass(attribute: AttributeDto): void {
    this.attributedLoading = true;
    this.instancesService.setClassAttribute(attribute.id)
      .pipe(
        finalize(() => {
          this.attributedLoading = false;
        })
      )
      .subscribe({
        next: () => {
          this.getInstancesDetails();
        },
        error: (error) => {
          this.handleSetClassError(error);
        }
      });
  }

  public getAttributes(): void {
    this.instancesService.getAttributes(this.id)
      .subscribe({
        next: (attributes: AttributeDto[]) => {
          this.attributes = attributes;
          this.setClassIfAbsent();
          this.columns = attributes.map((attr: AttributeDto) => { return { name: attr.name, label: attr.name} });
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<string[]>> {
    return this.instancesService.getDataPage(this.id, pageRequest);
  }

  public getColumnValueByIndex(column: number, item: string[]): any {
    return item[column];
  }

  public onDeleteInstances(): void {
    this.confirmationService.confirm({
      message: 'Вы уверены?',
      acceptLabel: 'Да',
      rejectLabel: 'Нет',
      accept: () => {
        this.deleteInstances();
      }
    });
  }

  public onExportInstances(): void {
    this.exportInstancesModel = new ExportInstancesModel(this.instancesDto.id, this.instancesDto.tableName);
    this.exportInstancesDialogVisibility = true;
  }

  public onCreateEditInstancesDialogVisibility(visible): void {
    this.createEditInstancesDialogVisibility = visible;
  }

  public onExportInstancesDialogVisibility(visible): void {
    this.exportInstancesDialogVisibility = visible;
  }

  public renameInstances(): void {
    this.createEditInstancesModel = new CreateEditInstancesModel(this.id, this.instancesDto.tableName);
    this.createEditInstancesDialogVisibility = true;
  }

  public onRenameInstances(event): void {
    this.getInstancesDetails();
  }

  private setClassIfAbsent(): void {
    if (this.instancesDto.classAttributeId) {
      this.classAttribute = this.attributes.filter((attr: AttributeDto) => attr.id == this.instancesDto.classAttributeId).pop();
    } else {
      this.classAttribute = null;
    }
  }

  private deleteInstances(): void {
    this.instancesService.deleteInstances(this.id)
      .subscribe({
        next: () => {
          this.messageService.add({ severity: 'success',
            summary: `Данные ${this.instancesDto.tableName} были успешно удалены`, detail: '' });
          this.router.navigate(['/dashboard/instances']);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private selectAttribute(id: number): void {
    this.attributedLoading = true;
    this.instancesService.selectAttribute(id)
      .pipe(
        finalize(() => {
          this.attributedLoading = false;
        })
      )
      .subscribe({
        next: () => {
          this.getAttributes();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private unselectAttribute(id: number): void {
    this.attributedLoading = true;
    this.instancesService.unselectAttribute(id)
      .pipe(
        finalize(() => {
          this.attributedLoading = false;
        })
      )
      .subscribe({
        next: () => {
          this.getAttributes();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private handleSetClassError(error): void {
    this.attributesComponent.forceSetClass(this.classAttribute);
    const errorCode = this.errorHandler.getFirstErrorCode(error, this.errorCodes);
    if (errorCode) {
      const errorMessage = this.errorCodesMap.get(errorCode);
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: errorMessage });
    }
  }
}
