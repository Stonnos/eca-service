import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthService } from "../services/auth.service";
import { UserModel } from "./user.model";
import { NgForm } from "@angular/forms";
import { Router } from "@angular/router";
import { AuthenticationKeys } from "../model/auth.keys";
import { finalize } from "rxjs/internal/operators";
import { BaseForm } from "../../common/form/base-form";
import { HttpErrorResponse } from "@angular/common/http";
import { UserStorage } from "../services/user.storage";
import { MessageService } from "primeng/api";
import { UserDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { UsersService } from "../../users/services/users.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements BaseForm, OnInit {

  public errorMessage: string;
  public submitted: boolean = false;
  public loading: boolean = false;

  public userModel: UserModel = new UserModel();

  @ViewChild(NgForm, { static: true })
  public form: NgForm;

  public constructor(private router: Router,
                     private authService: AuthService,
                     public usersService: UsersService,
                     private userStorage: UserStorage,
                     private messageService: MessageService) {
  }

  public ngOnInit() {
    if (localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)) {
      this.enter();
    }
  }

  public enter(): void {
    this.router.navigate(['/dashboard/experiments']);
  }

  public clear(): void {
    this.submitted = false;
  }

  public isValid(): boolean {
    return this.form.valid;
  }

  public submit(): void {
    this.submitted = true;
    if (this.isValid()) {
      this.loading = true;
      this.authService.obtainAccessToken(this.userModel)
        .pipe(
          finalize(() => {
            this.loading = false;
          })
        )
        .subscribe({
          next: (token) => {
            this.authService.saveToken(token);
            this.saveUser();
          },
          error: (error) => {
            this.handleError(error);
          }
        });
      this.clear();
    }
  }

  private saveUser(): void {
    this.usersService.getCurrentUser().subscribe({
      next: (user: UserDto) => {
        this.userStorage.saveUser(user);
        this.enter();
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    })
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 400) {
        this.errorMessage = 'Неправильный логин или пароль';
      } else {
        this.errorMessage = 'Возникла неизвестная ошибка';
      }
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }
}
