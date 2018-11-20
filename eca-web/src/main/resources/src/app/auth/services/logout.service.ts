import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from "ngx-cookie-service";

@Injectable()
export class LogoutService {

  constructor(private router: Router, private cookieService: CookieService) {
  }

  public logout() {
    this.cookieService.delete('access_token');
    this.router.navigate(['/login']);
  }
}
