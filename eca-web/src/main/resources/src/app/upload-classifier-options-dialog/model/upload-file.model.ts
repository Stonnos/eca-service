import { UploadStatus } from "./upload-status.enum";

export class UploadFileModel {
  file: File;
  status: UploadStatus;

  constructor(file: File, status: UploadStatus) {
    this.file = file;
    this.status = status;
  }
}
