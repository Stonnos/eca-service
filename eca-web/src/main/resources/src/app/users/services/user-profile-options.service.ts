import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  UserProfileNotificationOptionsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { UpdateUserProfileNotificationOptionsRequest } from "../model/update-user-profile-notification-options-request.model";

@Injectable()
export class UserProfileOptionsService {

  private serviceUrl = environment.oauthUrl + '/user/profile/options';

  public constructor(private http: HttpClient) {
  }

  public getUserNotificationOptions(): Observable<UserProfileNotificationOptionsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<UserProfileNotificationOptionsDto>(this.serviceUrl + '/notifications', { headers: headers });
  }

  public updateUserProfileNotificationOptions(updateUserProfileNotificationOptionsRequest: UpdateUserProfileNotificationOptionsRequest) {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.put(this.serviceUrl + '/update-notifications', updateUserProfileNotificationOptionsRequest, { headers: headers })
  }
}
