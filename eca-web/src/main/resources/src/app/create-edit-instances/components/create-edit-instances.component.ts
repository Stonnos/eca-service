import { Component, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { UploadTrainingDataComponent } from "../../common/upload-training-data/upload-training-data.component";
import { CreateEditInstancesModel } from "../model/create-edit-instances.model";
import { MessageService } from "primeng/api";
import { ValidationService } from "../../common/services/validation.service";
import { InstancesService } from "../../instances/services/instances.service";
import { finalize} from "rxjs/operators";
import {
  CreateInstancesResultDto,
  ValidationErrorDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { HttpErrorResponse} from "@angular/common/http";
import { InstancesFields } from "../../common/util/field-names";
import { ValidationErrorCode} from "../../common/model/validation-error-code";

@Component({
  selector: 'app-create-edit-instances',
  templateUrl: './create-edit-instances.component.html',
  styleUrls: ['./create-edit-instances.component.scss']
})
export class CreateEditInstancesComponent extends BaseCreateDialogComponent<CreateEditInstancesModel> implements OnInit {

  public tableNameRegex: string = '[0-9a-z_]+';

  public loading: boolean = false;

  public hasSameTableName: boolean = false;

  @ViewChild(UploadTrainingDataComponent, { static: true })
  public fileUpload: UploadTrainingDataComponent;

  @Output()
  public renameEvent: EventEmitter<any> = new EventEmitter<any>();

  public constructor(private instancesService: InstancesService,
                     private messageService: MessageService,
                     private validationService: ValidationService) {
    super();
  }

  public ngOnInit(): void {
  }

  public isValid(): boolean {
    return super.isValid() && (this.isEditMode() || this.fileUpload.isSelected());
  }

  public submit() {
    this.submitted = true;
    if (this.isValid()) {
      if (this.isEditMode()) {
        this.renameData();
      } else {
        this.saveData();
      }
    }
  }

  private renameData(): void {
    this.loading = true;
    this.instancesService.renameData(this.item.id, this.item.tableName)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.renameEvent.emit();
          this.hide();
        },
        error: (error) => {
          this.handleError(error);
        }
      });
  }

  private saveData(): void {
    this.loading = true;
    this.instancesService.saveData(this.item.file, this.item.tableName)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (createInstancesResultDto: CreateInstancesResultDto) => {
          this.itemEvent.emit(createInstancesResultDto);
          this.hide();
        },
        error: (error) => {
          this.handleError(error);
        }
      });
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 400) {
        const errors: ValidationErrorDto[] = error.error;
        this.hasSameTableName = this.validationService.hasError(errors, InstancesFields.TABLE_NAME, ValidationErrorCode.UNIQUE_TABLE_NAME);
      } else {
        this.handleUnknownError(error);
      }
    } else {
      this.handleUnknownError(error);
    }
  }

  private handleUnknownError(error): void {
    this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
  }

  public clear(): void {
    this.item.file = null;
    this.fileUpload.clearAll();
    super.clear();
  }

  public onUpload(file: File): void {
    this.item.file = file;
    this.fileUpload.resetUpload();
  }

  public onTableNameFocus(event): void {
    this.hasSameTableName = false;
  }

  public isEditMode(): boolean {
    return !!this.item.id;
  }
}
