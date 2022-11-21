import { Injectable } from "@angular/core";
import { WsService } from "../websockets/ws.service";
import { Subject } from "rxjs/internal/Subject";
import { PushRequestDto, PushTokenDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { environment } from "../../../environments/environment";
import { Logger } from "../util/logging";
import { Observable } from "rxjs/internal/Observable";
import { filter } from "rxjs/internal/operators";
import { MessageService } from "primeng/api";
import { Subscription } from "rxjs";
import { PushTokenService } from "./push-token.service";

@Injectable()
export class PushService {

  private messageSubject: Subject<PushRequestDto> = new Subject<PushRequestDto>();

  private messageSubscriptions: Subscription[] = [];

  private initialized: boolean = false;

  public constructor(private wsService: WsService,
                     private pushTokenService: PushTokenService,
                     private messageService: MessageService) {
  }

  public init() {
    if (this.initialized) {
      Logger.debug('Push service channel has been already initialized. Skipped...');
    } else {
      this.wsService.init();
      // Subscribes for system pushes queue
      this.subscribeForQueue(environment.pushQueue);
      // Subscribes for user push notifications
      this.subscribeForUserPushNotifications();
      this.initialized = true;
      Logger.debug('Push service channel has been initialized');
    }
  }

  public close() {
    if (!this.initialized) {
      Logger.debug('Push service channel has been already closed. Skipped...');
    } else {
      this.messageSubscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
      this.wsService.close();
      this.initialized = false;
      Logger.debug('Push service channel has been closed');
    }
  }

  public pushMessageSubscribe(predicate: (value: PushRequestDto, index: number) => boolean): Observable<PushRequestDto> {
    return this.messageSubject.asObservable()
      .pipe(
        filter(predicate)
      );
  }

  private subscribeForUserPushNotifications(): void {
    this.pushTokenService.obtainPushToken()
      .subscribe({
        next: (pushTokenDto: PushTokenDto) => {
          Logger.debug('Received user push token');
          const queue = `${environment.pushQueue}/${pushTokenDto.tokenId}`;
          this.subscribeForQueue(queue);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private subscribeForQueue(queue: string): void {
   const subscription = this.wsService.subscribe(queue)
      .subscribe({
        next: (message) => {
          Logger.debug(`Received web push ${message.body}`);
          const pushRequestDto: PushRequestDto = JSON.parse(message.body);
          if (pushRequestDto.showMessage) {
            this.showMessage(pushRequestDto);
          }
          this.messageSubject.next(pushRequestDto);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
   this.messageSubscriptions.push(subscription);
  }

  private showMessage(pushRequestDto: PushRequestDto): void {
    this.messageService.add(
      {
        key: 'push',
        severity: 'info',
        summary: pushRequestDto.messageText,
        detail: '',
        life: environment.pushLifeTimeMillis,
        data: pushRequestDto
      });
  }
}
