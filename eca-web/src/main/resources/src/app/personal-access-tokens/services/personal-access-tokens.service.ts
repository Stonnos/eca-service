import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifiersConfigurationDto,
  PageDto,
  PersonalAccessTokenDto,
  PersonalAccessTokenDetailsDto,
  SimplePageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { CreatePersonalAccessTokenDto } from '../model/create-personal-access-token.model';

@Injectable()
export class PersonalAccessTokensService {

  private serviceUrl = environment.oauthUrl + '/personal-access-token';

  public constructor(private http: HttpClient) {
  }

  public getTokens(pageRequest: SimplePageRequestDto): Observable<PageDto<PersonalAccessTokenDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<PageDto<PersonalAccessTokenDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getClassifiersConfigurationDetails(configurationId: number): Observable<ClassifiersConfigurationDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<ClassifiersConfigurationDto>(this.serviceUrl + '/details/' + configurationId.toString(), { headers: headers });
  }

  public createToken(personalAccessTokenDto: CreatePersonalAccessTokenDto): Observable<PersonalAccessTokenDetailsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<PersonalAccessTokenDetailsDto>(this.serviceUrl + '/create', personalAccessTokenDto, { headers: headers })
  }

  public deleteToken(id: number): Observable<any> {
    return this.http.delete(this.serviceUrl + '/remove/' + id);
  }
}
