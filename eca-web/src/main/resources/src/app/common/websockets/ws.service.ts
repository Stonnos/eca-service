import { StompConfig, StompService } from "@stomp/ng2-stompjs";
import { environment } from "../../../environments/environment";
import { Injectable } from "@angular/core";

@Injectable()
export class WsService {

  private wsUrl = environment.wsUrl;
  private stompService: StompService;

  public constructor() {
  }

  public init(): void {
    this.stompService = new StompService(this.getStompConfig());
  }

  public subscribe(queueName: string) {
    return this.stompService.subscribe(queueName);
  }

  public close(): void {
    this.stompService.disconnect();
  }

  private getStompConfig(): StompConfig {
    return {
      url: () => new WebSocket(this.wsUrl),
      headers: {
      },
      heartbeat_in: 0,
      heartbeat_out: 20000,
      reconnect_delay: 5000,
      debug: environment.debug
    };
  }
}
