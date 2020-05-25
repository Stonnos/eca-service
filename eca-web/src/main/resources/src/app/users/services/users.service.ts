import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  PageDto,
  PageRequestDto,
  UserDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { AuthenticationKeys } from "../../auth/model/auth.keys";
import { PageRequestService } from "../../common/services/page-request.service";
import { environment } from "../../../environments/environment";

@Injectable()
export class UsersService {

  private serviceUrl = environment.oauthUrl + '/users';

  public constructor(private http: HttpClient, private pageRequestService: PageRequestService) {
  }

  public getUsers(pageRequest: PageRequestDto): Observable<PageDto<UserDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest);
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<UserDto>>(this.serviceUrl + '/list', options);
  }

  public getCurrentUser(): Observable<UserDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<UserDto>(this.serviceUrl + '/user-info', { headers: headers });
  }
}
