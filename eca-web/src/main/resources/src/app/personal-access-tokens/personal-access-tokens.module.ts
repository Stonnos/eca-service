import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { PersonalAccessTokensComponent } from "./components/personal-access-tokens.component";
import { PersonalAccessTokensService } from "./services/personal-access-tokens.service";
import { SharedModule } from "../common/shared.module";
import { CreatePersonalAccessTokenComponent } from './create-token/create-personal-access-token.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    UiComponentsModule,
  ],
  declarations: [
    PersonalAccessTokensComponent,
    CreatePersonalAccessTokenComponent
  ],
  exports: [
    PersonalAccessTokensComponent
  ],
  providers: [
    PersonalAccessTokensService
  ]
})
export class PersonalAccessTokensModule {
}
