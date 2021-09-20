import { Injectable } from "@angular/core";
import { ValidationService } from "./validation.service";
import { MessageService } from "primeng/api";
import { HttpErrorResponse } from "@angular/common/http";

@Injectable()
export class ErrorHandler {

  public constructor(private validationService: ValidationService,
                     private messageService: MessageService) {
  }

  public getFirstErrorCode(error: any, errorCodes: string[]): string {
    if (error instanceof HttpErrorResponse && error.status === 400) {
      return this.validationService.getFirstErrorCode(error.error, errorCodes);
    } else {
      this.messageService.add({ severity: 'error', summary: 'Неизвестная ошибка', detail: error.message });
      return null;
    }
  }
}
