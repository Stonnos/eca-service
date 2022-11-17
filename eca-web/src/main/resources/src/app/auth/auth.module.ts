import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { LoginComponent } from "./components/login.component";
import { AuthService } from "./services/auth.service";
import { HTTP_INTERCEPTORS } from "@angular/common/http";
import { AuthInterceptor } from "./interceptors/auth-interceptor";
import { LogoutService } from "./services/logout.service";
import { SharedModule } from "../common/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    LoginComponent
  ],
  providers: [
    AuthService,
    LogoutService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
})
export class AuthModule {
}
