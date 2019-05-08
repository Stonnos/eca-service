import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthService } from "../services/auth.service";
import { UserModel } from "./user.model";
import { NgForm } from "@angular/forms";
import { Router } from "@angular/router";
import { AuthenticationKeys } from "../model/auth.keys";
import { finalize } from "rxjs/internal/operators";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public errorMessage: string;
  public submitted: boolean = false;
  public loading: boolean = false;

  public userModel: UserModel = new UserModel();

  @ViewChild(NgForm)
  public form: NgForm;

  constructor(private router: Router, private authService: AuthService) {

  }

  public ngOnInit() {
    if (this.authService.hasAccessToken()) {
      this.giveAccess();
    }
  }

  public onSubmit(event) {
    this.submitted = true;
    if (this.form.valid) {
      this.loading = true;
      this.authService.obtainAccessToken(this.userModel)
        .pipe(
          finalize(() => {
            this.loading = false;
          })
        )
        .subscribe((token) => {
          this.authService.saveToken(token);
          this.saveUser();
          this.giveAccess();
        }, (error) => {
          this.errorMessage = "Неправильный логин или пароль";
        });
      this.submitted = false;
    }
  }

  public giveAccess(): void {
    this.router.navigate(['/dashboard/experiments']);
  }

  private saveUser(): void {
    localStorage.setItem(AuthenticationKeys.USER_NAME, this.userModel.login);
  }

}
