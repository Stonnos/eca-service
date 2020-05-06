import { Component, Input, OnInit } from '@angular/core';
import { EnumDto } from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-request-status',
  templateUrl: './request-status.component.html',
  styleUrls: ['./request-status.component.scss']
})
export class RequestStatusComponent implements OnInit {

  @Input()
  public requestStatus: EnumDto;

  public ngOnInit() {
  }
}
