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

  public getNewRequestsCount(): number {
    return this.requestStatusStatisticsDto && this.requestStatusStatisticsDto.newRequestsCount;
  }

  public getFinishedRequestsCount(): number {
    return this.requestStatusStatisticsDto && this.requestStatusStatisticsDto.finishedRequestsCount;
  }

  public getTimeoutRequestsCount(): number {
    return this.requestStatusStatisticsDto && this.requestStatusStatisticsDto.timeoutRequestsCount;
  }

  public getErrorRequestsCount(): number {
    return this.requestStatusStatisticsDto && this.requestStatusStatisticsDto.errorRequestsCount;
  }

  public getTotalRequestsCount(): number {
    return this.requestStatusStatisticsDto && this.requestStatusStatisticsDto.totalCount;
  }
}
