import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  PageDto,
  PageRequestDto,
  UserDto,
  UserDictionaryDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { CreateUserModel } from "../../create-user/model/create-user.model";
import { Utils } from "../../common/util/utils";
import { UpdateUserInfoModel } from "../model/update-user-info.model";

@Injectable()
export class UsersService {

  private serviceUrl = environment.oauthUrl + '/users';

  public constructor(private http: HttpClient) {
  }

  public getUsers(pageRequest: PageRequestDto): Observable<PageDto<UserDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PageDto<UserDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getUsersDictionary(pageRequest: PageRequestDto): Observable<PageDto<UserDictionaryDto>> {
      const headers = new HttpHeaders({
        'Content-type': 'application/json; charset=utf-8',
        'Authorization': Utils.getBearerTokenHeader()
      });
      return this.http.post<PageDto<UserDictionaryDto>>(this.serviceUrl + '/users-dictionary', pageRequest, { headers: headers });
    }

  public getCurrentUser(): Observable<UserDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<UserDto>(this.serviceUrl + '/user-info', { headers: headers });
  }

  public createUser(createUser: CreateUserModel): Observable<UserDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<UserDto>(this.serviceUrl + '/create', createUser, { headers: headers })
  }

  public updateUserInfo(updateUserInfo: UpdateUserInfoModel) {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.put(this.serviceUrl + '/update-info', updateUserInfo, { headers: headers })
  }

  public setTfaEnabled(tfaEnabled: boolean) {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('enabled', tfaEnabled.toString());
    return this.http.post(this.serviceUrl + '/tfa', formData, { headers: headers })
  }

  public uploadPhoto(file: File): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post(this.serviceUrl + '/upload-photo', formData, { headers: headers });
  }

  public deletePhoto(): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.delete(this.serviceUrl + '/delete-photo', { headers: headers });
  }

  public downloadPhoto(id: number): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const options = { headers: headers, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/photo/' + id.toString(), options);
  }

  public lock(userId: number) {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('userId', userId.toString());
    return this.http.post(this.serviceUrl + '/lock', formData, { headers: headers })
  }

  public unlock(userId: number) {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('userId', userId.toString());
    return this.http.post(this.serviceUrl + '/unlock', formData, { headers: headers })
  }

  public setPushEnabled(enabled: boolean) {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('enabled', enabled.toString());
    return this.http.post(this.serviceUrl + '/push-notifications/enabled', formData, { headers: headers })
  }
}
