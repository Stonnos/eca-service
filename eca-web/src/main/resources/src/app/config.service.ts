import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { AppConfig } from "./config.model";
import { PlatformLocation } from "@angular/common";

@Injectable()
export class ConfigService {

  public static appConfig: AppConfig;

  constructor(private httpClient: HttpClient, private platformLocation: PlatformLocation) {}

  getConfigs(): Promise<AppConfig> {
    const configUrl: string = this.platformLocation.getBaseHrefFromDOM() + 'config';
    return this.httpClient.get<AppConfig>(configUrl).toPromise().then(appConfig => {
      ConfigService.appConfig = appConfig;
      return appConfig;
    });
  }
}
