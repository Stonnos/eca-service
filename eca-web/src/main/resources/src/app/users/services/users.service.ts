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
import { UpdateUserInfoModel } from "../model/update-user-info.model";

@Injectable()
export class UsersService {

  private serviceUrl = environment.oauthUrl + '/users';

  public constructor(private http: HttpClient) {
  }

  public getUsers(pageRequest: PageRequestDto): Observable<PageDto<UserDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<PageDto<UserDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getUsersDictionary(pageRequest: PageRequestDto): Observable<PageDto<UserDictionaryDto>> {
      const headers = new HttpHeaders({
        'Content-type': 'application/json; charset=utf-8'
      });
      return this.http.post<PageDto<UserDictionaryDto>>(this.serviceUrl + '/users-dictionary', pageRequest, { headers: headers });
    }

  public getCurrentUser(): Observable<UserDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<UserDto>(this.serviceUrl + '/user-info', { headers: headers });
  }

  public createUser(createUser: CreateUserModel): Observable<UserDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<UserDto>(this.serviceUrl + '/create', createUser, { headers: headers })
  }

  public updateUserInfo(updateUserInfo: UpdateUserInfoModel) {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.put(this.serviceUrl + '/update-info', updateUserInfo, { headers: headers })
  }

  public setTfaEnabled(tfaEnabled: boolean) {
    const formData = new FormData();
    formData.append('enabled', tfaEnabled.toString());
    return this.http.post(this.serviceUrl + '/tfa', formData)
  }

  public uploadPhoto(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post(this.serviceUrl + '/upload-photo', formData);
  }

  public deletePhoto(): Observable<any> {
    return this.http.delete(this.serviceUrl + '/delete-photo');
  }

  public downloadPhoto(id: number): Observable<Blob> {
    const options = { responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/photo/' + id.toString(), options);
  }

  public lock(userId: number) {
    const formData = new FormData();
    formData.append('userId', userId.toString());
    return this.http.post(this.serviceUrl + '/lock', formData)
  }

  public unlock(userId: number) {
    const formData = new FormData();
    formData.append('userId', userId.toString());
    return this.http.post(this.serviceUrl + '/unlock', formData)
  }

  public setPushEnabled(enabled: boolean) {
    const formData = new FormData();
    formData.append('enabled', enabled.toString());
    return this.http.post(this.serviceUrl + '/push-notifications/enabled', formData)
  }
}
