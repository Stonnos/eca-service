import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { UserModel } from "../components/user.model";
import { AuthenticationKeys } from "../model/auth.keys";

@Injectable()
export class AuthService {

  private serviceUrl = ConfigService.appConfig.oauthUrl;
  private clientId = ConfigService.appConfig.clientId;
  private secret = ConfigService.appConfig.secret;

  constructor(private http: HttpClient) {
  }

  public obtainAccessToken(user: UserModel): Observable<any> {
    const params = new URLSearchParams();
    params.append('username', user.login);
    params.append('password', user.password);
    params.append('grant_type', 'password');
    params.append('client_id', this.clientId);
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': 'Basic ' + btoa(this.clientId + ':' + this.secret)
    });
    const options = { headers: headers };
    return this.http.post(this.serviceUrl, params.toString(), options);
  }

  public saveToken(token){
    const expireDate = new Date().getTime() + (1000 * token.expires_in);
    localStorage.setItem(AuthenticationKeys.ACCESS_TOKEN, token.access_token);
    localStorage.setItem(AuthenticationKeys.EXPIRE_DATE, expireDate.toString());
  }

  public hasAccessToken(): boolean {
    const expireDate = localStorage.getItem(AuthenticationKeys.EXPIRE_DATE);
    return expireDate && new Date().getTime() < Number(expireDate);
  }
}
