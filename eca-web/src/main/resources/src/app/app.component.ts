import { Component } from '@angular/core';
import { PushRequestDto } from "../../../../../target/generated-sources/typescript/eca-web-dto";
import { Router } from "@angular/router";
import { RouterPaths } from "./routing/router-paths";
import { PushMessageType } from "./common/util/push-message.type";
import { PushVariables } from "./common/util/push-variables";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  public constructor(private router: Router) {
  }

  public onLinkClick(pushRequestDto: PushRequestDto): void {
    if (pushRequestDto.messageType == PushMessageType.EXPERIMENT_STATUS_CHANGE) {
      const id = pushRequestDto.additionalProperties[PushVariables.EXPERIMENT_ID];
      this.router.navigate([RouterPaths.EXPERIMENT_DETAILS_URL, id]);
    }
  }
}
