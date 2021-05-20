import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AuthService } from "../services/auth.service";
import { UserModel } from "../model/user.model";
import { NgForm } from "@angular/forms";
import { Router } from "@angular/router";
import { AuthenticationKeys } from "../model/auth.keys";
import { finalize } from "rxjs/internal/operators";
import { BaseForm } from "../../common/form/base-form";
import { HttpErrorResponse } from "@angular/common/http";
import { MessageService } from "primeng/api";
import { UsersService } from "../../users/services/users.service";
import { Subscription, timer } from "rxjs";
import {StompConfig, StompService} from "@stomp/ng2-stompjs";

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

  public codeExpired: boolean = false;

  private readonly timerInterval = 1000;

  private updateCodeValidityTimeSubscription: Subscription = new Subscription();

  public constructor(private router: Router,
                     private authService: AuthService,
                     private usersService: UsersService,
                     private messageService: MessageService) {
  }

  public ngOnInit() {
    if (localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)) {
      this.enter();
    }
    let stompConfig: StompConfig = {
      url: 'ws://localhost:8085/socket',
      headers: {
      },
      heartbeat_in: 0,
      heartbeat_out: 20000,
      reconnect_delay: 5000,
      debug: true
    };
    const stompService = new StompService(stompConfig);
    stompService.subscribe('/queue/experiment')
      .subscribe({
        next: (message) => {
          console.log(message.body);
        },
        error: (error) => {
          console.log(error);
        }
      });
  }

  public ngOnDestroy(): void {
    this.updateCodeValidityTimeSubscription.unsubscribe();
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

  public resetTfaCodeVerification(): void {
    this.tfaCodeVerificationStep = false;
    this.tfaVerificationCode = null;
    this.errorMessage = null;
    this.userModel = new UserModel();
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
          this.enter();
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
          this.enter();
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
        this.codeExpired = false;
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
    this.updateCodeValidityTimeSubscription = timer(0, this.timerInterval).subscribe({
      next: () => {
        this.codeValidityCurrentTime--;
        if (this.codeValidityCurrentTime <= 0) {
          this.codeExpired = true;
          this.errorMessage = null;
          this.updateCodeValidityTimeSubscription.unsubscribe();
        }
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    });
  }
}
