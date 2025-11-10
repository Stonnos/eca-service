import { Component, OnInit } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { MessageService, SelectItem } from 'primeng/api';
import { ValidationService } from "../../common/services/validation.service";
import { finalize} from "rxjs/operators";
import {
  PersonalAccessTokenDetailsDto,
  ValidationErrorDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { HttpErrorResponse } from "@angular/common/http";
import { ValidationErrorCode } from "../../common/model/validation-error-code";
import { CreatePersonalAccessTokenDto, CreatePersonalAccessTokenModel } from '../model/create-personal-access-token.model';
import { PersonalAccessTokensService } from '../services/personal-access-tokens.service';

@Component({
  selector: 'app-create-personal-access-token',
  templateUrl: './create-personal-access-token.component.html',
  styleUrls: ['./create-personal-access-token.component.scss']
})
export class CreatePersonalAccessTokenComponent extends BaseCreateDialogComponent<CreatePersonalAccessTokenModel> implements OnInit {

  public loading: boolean = false;

  public hasDuplicateTokenName: boolean = false;

  public tokenTypes: SelectItem[] = [
    { label: 'Токен пользователя', value: 'USER_TOKEN'},
    { label: 'Токен приложения', value: 'APP_TOKEN'}
  ];

  public months: SelectItem[] = [
    { label: '1 месяц', value: 1},
    { label: '3 месяца', value: 3},
    { label: '6 месяца', value: 6},
    { label: '12 месяцев', value: 12}
  ];

  public constructor(private personalAccessTokensService: PersonalAccessTokensService,
                     private messageService: MessageService,
                     private validationService: ValidationService) {
    super();
  }

  public ngOnInit(): void {
  }

  public onTokenNameFocus(event): void {
    this.hasDuplicateTokenName = false;
  }

  public submit() {
    this.submitted = true;
    if (this.isValid()) {
      this.createToken();
    }
  }

  private createToken(): void {
    this.loading = true;
    const personalAccessTokenRequest =
      new CreatePersonalAccessTokenDto(this.item.name, this.item.tokenType.value, this.item.expirationMonth.value);
    this.personalAccessTokensService.createToken(personalAccessTokenRequest)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (personalAccessTokenDetailsDto: PersonalAccessTokenDetailsDto) => {
          this.itemEvent.emit(personalAccessTokenDetailsDto);
          this.hide();
        },
        error: (error) => {
          this.handleError(error);
        }
      });
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse && error.status === 400) {
      const errors: ValidationErrorDto[] = error.error;
      this.hasDuplicateTokenName = this.validationService.hasErrorCode(errors, ValidationErrorCode.DUPLICATE_PERSONAL_ACCESS_TOKEN_NAME);
    } else {
      this.messageService.add({ severity: 'error', summary: 'Не удалось создать токен', detail: error.message });
    }
  }
}
