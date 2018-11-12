import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from "rxjs/internal/Observable";
import { CookieService } from "ngx-cookie-service";
import { ConfigService } from "./config.service";

@Injectable()
export class AuthService {

  private serviceUrl = ConfigService.appConfig.oauthUrl;
  private clientId = ConfigService.appConfig.clientId;
  private secret = ConfigService.appConfig.secret;

  constructor(private router: Router, private http: HttpClient, private cookieService: CookieService) {
  }

  obtainAccessToken(username: string, password: string): Observable<any> {
    const params = new URLSearchParams();
    params.append('username', username);
    params.append('password', password);
    params.append('grant_type', 'password');
    params.append('client_id', this.clientId);
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': 'Basic ' + btoa(this.clientId + ':' + this.secret)
    });
    const options = { headers: headers };
    return this.http.post(this.serviceUrl, params.toString(), options);
  }

  saveToken(token){
    const expireDate = new Date().getTime() + (1000 * token.expires_in);
    this.cookieService.set("access_token", token.access_token, expireDate);
    this.router.navigate(['/']);
  }

  checkCredentials(){
    if (!this.cookieService.check('access_token')){
      this.router.navigate(['/login']);
    }
  }

  logout() {
    this.cookieService.delete('access_token');
    this.router.navigate(['/login']);
  }
}
