import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FileUpload } from "primeng/primeng";
import { ClassifierOptionsService } from "../../classifier-options/services/classifier-options.service";
import { CreateClassifierOptionsResultDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { forkJoin } from 'rxjs';
import { MessageService } from "primeng/api";
import { finalize } from "rxjs/internal/operators";

@Component({
  selector: 'app-upload-classifier-options-dialog',
  templateUrl: './upload-classifier-options-dialog.component.html',
  styleUrls: ['./upload-classifier-options-dialog.component.scss']
})
export class UploadClassifierOptionsDialogComponent implements OnInit {

  //Max file size: 10kb
  public maxFileSize: number = 10000;
  //Files limit
  public filesLimit: number = 10;
  //Files formats
  public accept: string = '.json';
  public invalidFileSizeMessageSummary: string = 'Недопустимый размер файла,';
  public invalidFileSizeMessageDetail: string = 'максимальный допустимый размер: {0}.';
  public invalidFileTypeMessageSummary: string = 'Некорректный тип файла,';
  public invalidFileTypeMessageDetail: string = 'допускаются только файлы форматов: {0}.';
  public invalidFileLimitMessageDetail: string = 'максимальное допустимое число: {0}';
  public invalidFileLimitMessageSummary: string = 'Превышено максимальное число файлов';

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

  public onUpload(event: any): void {
    this.uploadProgress = true;
    this.uploadedFiles = [];
    const observables: Observable<CreateClassifierOptionsResultDto>[] = this.initializeObservables(event);
    this.fileUpload.clear();
    forkJoin(observables)
      .pipe(
        finalize(() => {
          this.uploadProgress = false;
        })
      )
      .subscribe({
        next: (results: CreateClassifierOptionsResultDto[]) => {
          this.uploadedFiles = results;
          this.uploadProgress = false;
          this.uploaded.emit();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
    });
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
      observables.push(this.classifierOptionsService.saveClassifierOptions(this.configurationId, file));
    }
    return observables;
  }
}
