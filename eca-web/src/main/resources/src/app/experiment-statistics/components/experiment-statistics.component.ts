import { Component } from '@angular/core';
import { ChartDto} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { Observable } from "rxjs/internal/Observable";

@Component({
  selector: 'app-experiment-statistics',
  templateUrl: './experiment-statistics.component.html',
  styleUrls: ['./experiment-statistics.component.scss']
})
export class ExperimentStatisticsComponent {

  public constructor(private experimentsService: ExperimentsService) {
  }

  public getExperimentsStatisticsChartData(): (from: string, to: string) => Observable<ChartDto> {
    return (from: string, to: string) => this.experimentsService.getExperimentsStatistics(from, to);
  }
}
