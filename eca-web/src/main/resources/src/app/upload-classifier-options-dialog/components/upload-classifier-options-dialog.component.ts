import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FileUpload } from "primeng/primeng";
import { ClassifierOptionsService } from "../../classifier-options/services/classifier-options.service";
import { UploadFileModel } from "../model/upload-file.model";
import { UploadStatus } from "../model/upload-status.enum";

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
  public invalidFileSizeMessageDetail: string = 'максимальный допустимый размер: {0}.';
  public invalidFileTypeMessageSummary: string = 'Некорректный тип файла,';
  public invalidFileTypeMessageDetail: string = 'допускаются только файлы форматов: {0}.';

  @Input()
  public visible: boolean = false;
  @Input()
  public configurationId: number;

  @ViewChild(FileUpload, { static: true })
  public fileUpload: FileUpload;

  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output()
  public uploaded: EventEmitter<any> = new EventEmitter<any>();

  public uploadedFiles: UploadFileModel[] = [];

  public constructor(private classifierOptionsService: ClassifierOptionsService) {
  }

  public ngOnInit(): void {
  }

  public hide(): void {
    this.uploadedFiles = [];
    this.fileUpload.clear();
    this.visibilityChange.emit(false);
  }

  public onUpload(event: any): void {
    this.uploadedFiles = [];
    for (let fileName in event.files) {
      const file: File = event.files[fileName];
      this.uploadedFiles.push(new UploadFileModel(file, UploadStatus.SUCCESS));
    }
    this.fileUpload.clear();
  }
}
