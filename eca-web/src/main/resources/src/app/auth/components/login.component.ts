import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
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
import { UsersService } from "../../users/services/users.service";
import { Subscription, timer } from "rxjs";
import { UserDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements BaseForm, OnInit, OnDestroy {

  public errorMessage: string;
  public submitted: boolean = false;
  public loading: boolean = false;
  public tfaCodeVerificationStep: boolean = false;

  public userModel: UserModel = new UserModel();

  public tfaVerificationCode: string;

  @ViewChild(NgForm, { static: false })
  public form: NgForm;

  public codeValidityCurrentTime: number;

  private updateCodeValiditySubscription: Subscription = new Subscription();

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

  public ngOnDestroy(): void {
    this.updateCodeValiditySubscription.unsubscribe();
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

  public getFormattedCurrentTokenValidityTime(): string {
    const minutes = Math.floor(this.codeValidityCurrentTime / 60);
    if (minutes == 0) {
      return `${this.codeValidityCurrentTime} сек `;
    } else {
      const seconds = this.codeValidityCurrentTime % 60;
      return `${minutes} мин ${seconds} сек`;
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

  private obtainToken(): void {
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

  private verifyCode(): void {
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
        this.startTfaCodeValidityTimer(error.error.expires_in);
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

  private startTfaCodeValidityTimer(expiresIn: number): void {
    this.codeValidityCurrentTime = expiresIn;
    this.updateCodeValiditySubscription = timer(0, 1000).subscribe({
      next: () => {
        this.codeValidityCurrentTime--;
        if (this.codeValidityCurrentTime <= 0) {
          this.codeValidityCurrentTime = null;
          this.updateCodeValiditySubscription.unsubscribe();
        }
      },
      error: (error) => {
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
      }
    });
  }
}
