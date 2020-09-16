import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthService } from "../services/auth.service";
import { UserModel } from "../model/user.model";
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
  public tfaCodeVerificationStep: boolean = false;

  public userModel: UserModel = new UserModel();

  public tfaVerificationCode: string;

  @ViewChild(NgForm, { static: false })
  public form: NgForm;

  public constructor(private router: Router,
                     private authService: AuthService,
                     private usersService: UsersService,
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
    return this.form && this.form.valid;
  }

  public submit(): void {
    this.submitted = true;
    if (this.isValid()) {
      if (this.tfaCodeVerificationStep) {
        this.verifyCode();
      } else {
        this.obtainToken();
      }
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

  public obtainToken(): void {
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
          this.handleObtainTokenError(error);
        }
      });
  }

  public verifyCode(): void {
    this.loading = true;
    this.authService.verifyTfaCode(this.tfaVerificationCode)
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
          this.handleTfaVerificationError(error);
        }
      });
  }

  private handleObtainTokenError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 400) {
        this.errorMessage = 'Неправильный логин или пароль';
      } else if (error.status === 403) {
        this.errorMessage = null;
        this.tfaCodeVerificationStep = true;
      } else {
        this.errorMessage = 'Возникла неизвестная ошибка';
      }
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }

  private handleTfaVerificationError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 400) {
        this.errorMessage = 'Неправильный код';
      } else {
        this.errorMessage = 'Возникла неизвестная ошибка';
      }
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }
}
