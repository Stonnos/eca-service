import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SharedModule } from "../common/shared.module";
import { SetPasswordComponent } from './components/set-password.component';
import { ForceSetPasswordService } from './services/force-set-password.service';
import { UiComponentsModule } from '../ui-components.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    SetPasswordComponent
  ],
  exports: [
    SetPasswordComponent
  ],
  providers: [
    ForceSetPasswordService
  ]
})
export class SetPasswordModule {
}
