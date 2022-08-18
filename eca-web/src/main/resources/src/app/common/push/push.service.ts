import { Injectable } from "@angular/core";
import { WsService } from "../websockets/ws.service";
import { Subject } from "rxjs/internal/Subject";
import { PushRequestDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { environment } from "../../../environments/environment";
import { Logger } from "../util/logging";
import { Observable } from "rxjs/internal/Observable";
import { filter } from "rxjs/internal/operators";
import { MessageService } from "primeng/api";
import { Subscription } from "rxjs";

@Injectable()
export class PushService {

  private messageSubject: Subject<PushRequestDto> = new Subject<PushRequestDto>();

  private messageSubscription: Subscription = new Subscription();

  public constructor(private wsService: WsService,
                     private messageService: MessageService) {
  }

  public init() {
    this.wsService.init();
    this.messageSubscription = this.wsService.subscribe(environment.pushQueue)
      .subscribe({
        next: (message) => {
          Logger.debug(`Received web push ${message.body}`);
          const pushRequestDto: PushRequestDto = JSON.parse(message.body);
          this.showMessage(pushRequestDto);
          this.messageSubject.next(pushRequestDto);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
    Logger.debug('Push channel has been initialized');
    return this.messageSubscription;
  }

  public close() {
    this.messageSubscription.unsubscribe();
    this.wsService.close();
    Logger.debug('Push channel has been closed');
  }

  public pushMessageSubscribe(predicate: (value: PushRequestDto, index: number) => boolean): Observable<PushRequestDto> {
    return this.messageSubject.asObservable()
      .pipe(
        filter(predicate)
      );
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
