import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationKeys } from "../model/auth.keys";

@Injectable()
export class LogoutService {

  constructor(private router: Router) {
  }

  public logout() {
    localStorage.removeItem(AuthenticationKeys.USER_NAME);
    localStorage.removeItem(AuthenticationKeys.ACCESS_TOKEN);
    localStorage.removeItem(AuthenticationKeys.EXPIRE_DATE);
    this.router.navigate(['/login']);
  }
}
