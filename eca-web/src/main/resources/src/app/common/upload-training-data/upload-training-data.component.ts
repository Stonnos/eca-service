import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FileUpload } from "primeng/primeng";

@Component({
  selector: 'app-upload-training-data',
  templateUrl: './upload-training-data.component.html',
  styleUrls: ['./upload-training-data.component.scss']
})
export class UploadTrainingDataComponent implements OnInit {

  //Max file size: 10MB
  public maxFileSize: number = 10000000;
  //Files formats
  public accept: string = '.csv,.xls,.xlsx,.arff,.xml,.json,.txt,.data,.docx';
  public invalidFileSizeMessageSummary: string = 'Недопустимый размер файла,';
  public invalidFileSizeMessageDetail: string = 'максимальный допустимый размер: {0}.';
  public invalidFileTypeMessageSummary: string = 'Некорректный тип файла,';
  public invalidFileTypeMessageDetail: string = 'допускаются только файлы форматов: {0}.';

  @Input()
  public submitted: boolean = false;

  @Input()
  public disabled: boolean = false;

  public trainingDataFile: File;

  @ViewChild(FileUpload, { static: true })
  public fileUpload: FileUpload;

  @Output()
  public upload: EventEmitter<File> = new EventEmitter<File>();

  public ngOnInit() {
  }

  public onUpload(event: any): void {
    this.trainingDataFile = event.files[0];
    this.fileUpload.clear();
    this.upload.emit(this.trainingDataFile);
  }

  public isSelected(): boolean {
    return this.trainingDataFile != null;
  }

  public resetUpload(): void {
    this.fileUpload.clear();
  }

  public clearAll(): void {
    this.resetUpload();
    this.trainingDataFile = null;
    this.fileUpload.msgs = [];
  }
}
