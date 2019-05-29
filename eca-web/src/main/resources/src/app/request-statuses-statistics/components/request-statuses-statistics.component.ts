import { Component, Input } from '@angular/core';
import { RequestStatusStatisticsDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-request-statuses-statistics',
  templateUrl: './request-statuses-statistics.html',
  styleUrls: ['./request-statuses-statistics.scss']
})
export class RequestStatusesStatisticsComponent {

  @Input()
  public requestStatusStatisticsDto: RequestStatusStatisticsDto;
}
