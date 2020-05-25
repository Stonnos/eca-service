import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { UsersListComponent } from "./components/users-list.component";
import { UsersService } from "./services/users.service";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
  ],
  declarations: [
    UsersListComponent
  ],
  exports: [
    UsersListComponent
  ],
  providers: [
    UsersService
  ]
})
export class UsersModule {
}
