import { EvaluationResultsAttachmentType } from '../model/evaluation-results-attachment-type.enum';
import { Observable } from "rxjs/internal/Observable";

export interface UploadEvaluationResultsAttachmentService {

  uploadEvaluationResultsAttachmentService(modelId: number, file: File, attachmentType: EvaluationResultsAttachmentType): Observable<any>
}
