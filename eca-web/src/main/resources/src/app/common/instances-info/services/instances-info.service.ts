import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  InstancesInfoDto, PageDto,
  PageRequestDto,
  AttributeValueMetaInfoDto,
  AttributeMetaInfoDto,
  InstancesInfoDetailsDto,
  S3ContentResponseDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { environment } from "../../../../environments/environment";
import { Observable } from "rxjs/internal/Observable";
import { InstancesInfoPageService } from "../../services/instances-info-page.service";
import { saveAs } from 'file-saver/dist/FileSaver';
import { catchError, finalize, switchMap } from 'rxjs/internal/operators';
import { EMPTY } from 'rxjs/internal/observable/empty';

@Injectable()
export class InstancesInfoService implements InstancesInfoPageService {

  private serviceUrl = environment.serverUrl + '/instances-info';

  public constructor(private http: HttpClient) {
  }

  public getInstancesInfoPage(pageRequest: PageRequestDto): Observable<PageDto<InstancesInfoDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<PageDto<InstancesInfoDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getClassValues(id: number): Observable<AttributeValueMetaInfoDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<AttributeValueMetaInfoDto[]>(this.serviceUrl + '/class-values/' + id, { headers: headers });
  }

  public getInputAttributes(id: number): Observable<AttributeMetaInfoDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<AttributeMetaInfoDto[]>(this.serviceUrl + '/input-attributes/' + id, { headers: headers });
  }

  public getInstancesInfoDetails(id: number): Observable<InstancesInfoDetailsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<InstancesInfoDetailsDto>(this.serviceUrl + '/details/' + id, { headers: headers });
  }

  public getInstancesDownloadUrl(id: number): Observable<S3ContentResponseDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<S3ContentResponseDto>(this.serviceUrl + '/download/' + id, { headers: headers });
  }

  public downloadInstances(instancesInfoDto: InstancesInfoDto, onSuccessCallback: () => void, onErrorCallback: (error: any) => void): void {
    this.getInstancesDownloadUrl(instancesInfoDto.id)
      .pipe(
        switchMap((s3ContentResponseDto: S3ContentResponseDto) => {
          return this.downloadContent(s3ContentResponseDto.contentUrl);
        }),
        catchError(error => {
          onErrorCallback(error);
          return EMPTY;
        }),
        finalize(() => {
          onSuccessCallback();
        })
      )
      .subscribe({
        next: (blob: Blob) => {
          saveAs(blob, `instances-${instancesInfoDto.uuid}.json`);
        },
        error: (error) => {
          onErrorCallback(error);
        }
      });
  }

  public downloadContent(url: string): Observable<Blob> {
    const options = { responseType: 'blob' as 'json' };
    return this.http.get<Blob>(url, options);
  }
}
