import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { AppConfig } from "./config.model";

@Injectable()
export class ConfigService {

  private configUrl: string = '/assets/configs/config.json';

  public static appConfig: AppConfig;

  constructor(private httpClient: HttpClient) {}

  getConfigs(): Promise<AppConfig> {
    return this.httpClient.get<AppConfig>(this.configUrl).toPromise().then(appConfig => {
      ConfigService.appConfig = appConfig;
      return appConfig;
    });
  }
}
