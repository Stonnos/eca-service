import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { UsersListComponent } from "./components/users-list.component";
import { UsersService } from "./services/users.service";
import { CreateUserModule } from "../create-user/create-user.module";
import { UserProfileOptionsService } from "./services/user-profile-options.service";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    CreateUserModule
  ],
  declarations: [
    UsersListComponent
  ],
  exports: [
    UsersListComponent
  ],
  providers: [
    UsersService,
    UserProfileOptionsService
  ]
})
export class UsersModule {
}
