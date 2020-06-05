import { Component, OnInit } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { CreateUserModel } from "../model/create-user.model";
import { UsersService } from "../../users/services/users.service";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { MessageService } from "primeng/api";
import { UserDto, ValidationErrorDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ValidationService } from "../../common/services/validation.service";
import { ValidationErrorCode } from "../../common/model/validation-error-code";
import { UserFields } from "../../common/util/field-names";

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss']
})
export class CreateUserComponent extends BaseCreateDialogComponent<CreateUserModel> implements OnInit {

  public loginRegex: string = '[a-z0-9]{3,}';
  public emailRegex: string = '[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})';
  public firstNameRegex: string = '([A-Z][a-z]+)|([А-Я][а-я]+)';

  public loading: boolean = false;

  public hasSameLogin: boolean = false;
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
      this.usersService.createUser(this.item)
        .pipe(
          finalize(() => {
            this.loading = false;
          })
        )
        .subscribe({
          next: (user: UserDto) => {
            this.itemEvent.emit(user);
            this.hide();
          },
          error: (error) => {
            this.handleError(error);
          }
        });
    }
  }

  public onLoginFocus(event): void {
    this.hasSameLogin = false;
  }

  public onEmailFocus(event): void {
    this.hasSameEmail = false;
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 400) {
        const errors: ValidationErrorDto[] = error.error;
        this.hasSameLogin = this.validationService.hasError(errors, UserFields.LOGIN, ValidationErrorCode.UNIQUE_LOGIN);
        this.hasSameEmail = this.validationService.hasError(errors, UserFields.EMAIL, ValidationErrorCode.UNIQUE_EMAIL);
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
