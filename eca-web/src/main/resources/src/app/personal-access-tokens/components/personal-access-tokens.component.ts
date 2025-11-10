import { Component, Injector, OnInit } from '@angular/core';
import {
  PersonalAccessTokenDto,
  PersonalAccessTokenDetailsDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ConfirmationService, MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";
import { PersonalAccessTokenFields } from '../../common/util/field-names';
import { FieldService } from "../../common/services/field.service";
import { PersonalAccessTokensService } from "../services/personal-access-tokens.service";
import { finalize } from "rxjs/internal/operators";
import { CreatePersonalAccessTokenModel } from '../model/create-personal-access-token.model';

@Component({
  selector: 'app-personal-access-tokens',
  templateUrl: './personal-access-tokens.component.html',
  styleUrls: ['./personal-access-tokens.component.scss']
})
export class PersonalAccessTokensComponent extends BaseListComponent<PersonalAccessTokenDto> implements OnInit {

  public createTokenDialogVisibility: boolean = false;

  public tokenInfoDialogVisibility: boolean = false;

  public createPersonalAccessTokenItem: CreatePersonalAccessTokenModel = new CreatePersonalAccessTokenModel();

  public token: string;

  public constructor(private injector: Injector,
                     private personalAccessTokensService: PersonalAccessTokensService,
                     private confirmationService: ConfirmationService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.initColumns();
  }

  public ngOnInit() {
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<PersonalAccessTokenDto>> {
    return this.personalAccessTokensService.getTokens(pageRequest);
  }

  public onCreateTokenDialogVisibility(visible): void {
    this.createTokenDialogVisibility = visible;
  }

  public hideTokenInfoDialogVisibility(): void {
    this.tokenInfoDialogVisibility = false;
    this.token = null;
  }

  public onDeleteToken(item: PersonalAccessTokenDto): void {
    this.confirmationService.confirm({
      message: 'Вы уверены, что хотите удалить токен?',
      acceptLabel: 'Да',
      rejectLabel: 'Нет',
      accept: () => {
        this.deleteToken(item);
      }
    });
  }

  public onCreatedToken(item: PersonalAccessTokenDetailsDto): void {
    this.token = item.token;
    this.tokenInfoDialogVisibility = true;
    this.reloadPageWithLoader();
  }

  public copyToClipboard(): void {
    navigator.clipboard.writeText(this.token);
  }

  private initColumns() {
    this.columns = [
      { name: PersonalAccessTokenFields.NAME, label: "Название" },
      { name: PersonalAccessTokenFields.EXPIRE_DATE, label: "Срок действия" }
    ];
  }

  private deleteToken(item: PersonalAccessTokenDto): void {
    this.loading = true;
    this.personalAccessTokensService.deleteToken(item.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: `Персональный токен доступа ${item.name} был удален`, detail: '' });
          this.reloadPageWithLoader();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }
}
