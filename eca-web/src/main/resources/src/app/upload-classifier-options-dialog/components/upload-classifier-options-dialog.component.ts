import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FileUpload } from "primeng/primeng";
import {
  CreateClassifierOptionsResultDto,
  ValidationErrorDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { forkJoin } from 'rxjs';
import { MessageService } from "primeng/api";
import { finalize } from "rxjs/internal/operators";
import { ClassifierOptionsService } from "../../classifiers-configuration-details/services/classifier-options.service";
import { ValidationErrorCode } from "../../common/model/validation-error-code";

@Component({
  selector: 'app-upload-classifier-options-dialog',
  templateUrl: './upload-classifier-options-dialog.component.html',
  styleUrls: ['./upload-classifier-options-dialog.component.scss']
})
export class UploadClassifierOptionsDialogComponent implements OnInit {

  //Max file size: 10kb
  public maxFileSize: number = 10000;
  //Files formats
  public accept: string = '.json';
  public invalidFileSizeMessageSummary: string = 'Недопустимый размер файла,';
  public invalidFileSizeMessageDetail: string = 'максимальный допустимый размер: 10 kb.';
  public invalidFileTypeMessageSummary: string = 'Некорректный тип файла,';
  public invalidFileTypeMessageDetail: string = 'допускаются только файлы форматов: {0}.';

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.INVALID_CLASSIFIER_OPTIONS_FORMAT, 'Неправильный формат настроек')
    .set(ValidationErrorCode.ENSEMBLE_CLASSIFIER_OPTIONS_NOT_ALLOWED, 'Допускаются только индивидуальные классификаторы')
    .set(ValidationErrorCode.INTERNAL_ERROR, 'Неизвестная ошибка');

  @Input()
  public visible: boolean = false;
  @Input()
  public configurationId: number;

  @ViewChild(FileUpload, { static: true })
  public fileUpload: FileUpload;

  public uploadProgress: boolean = false;

  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output()
  public uploaded: EventEmitter<any> = new EventEmitter<any>();

  public uploadedFiles: CreateClassifierOptionsResultDto[] = [];

  public constructor(private classifierOptionsService: ClassifierOptionsService,
                     private messageService: MessageService) {
  }

  public ngOnInit(): void {
  }

  public hide(): void {
    this.uploadedFiles = [];
    this.fileUpload.clear();
    this.fileUpload.msgs = [];
    this.visibilityChange.emit(false);
  }

  public onSelectFiles(event: any): void {
    this.uploadedFiles = [];
  }

  public onUpload(event: any): void {
    this.uploadProgress = true;
    this.uploadedFiles = [];
    this.fileUpload.clear();
    const observables: Observable<CreateClassifierOptionsResultDto>[] = this.initializeObservables(event);
    forkJoin(observables)
      .pipe(
        finalize(() => {
          this.uploadProgress = false;
        })
      )
      .subscribe({
        next: (results: CreateClassifierOptionsResultDto[]) => {
          this.uploadedFiles = results;
          this.uploaded.emit();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
    });
  }

  public getErrorMessage(error: ValidationErrorDto): string {
    return this.errorCodesMap.get(error.code);
  }

  public getSuccessfullyUploadedFilesCount(): number {
    return this.uploadedFiles.filter((result: CreateClassifierOptionsResultDto) => result.success).length;
  }

  public getUploadedWithErrorFilesCount(): number {
    return this.uploadedFiles.filter((result: CreateClassifierOptionsResultDto) => !result.success).length;
  }

  private initializeObservables(event: any): Observable<CreateClassifierOptionsResultDto>[] {
    const observables: Observable<CreateClassifierOptionsResultDto>[] = [];
    for (let fileName in event.files) {
      const file: File = event.files[fileName];
      observables.push(this.classifierOptionsService.uploadClassifierOptions(this.configurationId, file));
    }
    return observables;
  }
}
