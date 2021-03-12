import { Component, OnInit } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { UsersService } from "../../users/services/users.service";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { MessageService } from "primeng/api";
import { ValidationErrorDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ValidationService } from "../../common/services/validation.service";
import { ValidationErrorCode } from "../../common/model/validation-error-code";

@Component({
  selector: 'app-update-user-email',
  templateUrl: './update-user-email.component.html',
  styleUrls: ['./update-user-email.component.scss']
})
export class UpdateUserEmailComponent extends BaseCreateDialogComponent<string> implements OnInit {

  public emailRegex: string = '[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})';

  public loading: boolean = false;

  public hasSameEmail: boolean = false;

  public constructor(private usersService: UsersService,
                     private messageService: MessageService,
                     private validationService: ValidationService) {
    super();
  }

  public ngOnInit(): void {
  }

  public submit() {
    this.submitted = true;
    if (this.isValid()) {
      this.loading = true;
      this.usersService.updateEmail(this.item)
        .pipe(
          finalize(() => {
            this.loading = false;
          })
        )
        .subscribe({
          next: () => {
            this.itemEvent.emit();
            this.hide();
          },
          error: (error) => {
            this.handleError(error);
          }
        });
    }
  }

  public onEmailFocus(event): void {
    this.hasSameEmail = false;
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 400) {
        const errors: ValidationErrorDto[] = error.error;
        this.hasSameEmail = this.validationService.hasErrorCode(errors, ValidationErrorCode.UNIQUE_EMAIL);
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
}
