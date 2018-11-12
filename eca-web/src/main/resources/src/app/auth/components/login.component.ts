import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthService } from "../services/auth.service";
import { UserModel } from "./user.model";
import { NgForm } from "@angular/forms";
import { Router } from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public errorMessage: string;
  public submitted: boolean = false;

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
      this.authService.obtainAccessToken(this.userModel).subscribe((token) => {
          this.authService.saveToken(token);
          this.giveAccess();
        }, (error) => {
          console.log(error);
          this.errorMessage = "Login failed.";
        });
      this.submitted = false;
    }
  }

  public giveAccess() {
    this.router.navigate(['/dashboard/experiments']);
  }

}
