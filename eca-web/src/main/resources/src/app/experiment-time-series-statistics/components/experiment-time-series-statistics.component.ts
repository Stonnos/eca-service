import { Component } from '@angular/core';
import { ChartDto} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { TimeSeriesStatisticsFilter } from '../../common/model/time-series-statistics.model';
import { ExperimentsService } from '../../experiments/services/experiments.service';

@Component({
  selector: 'app-experiment-time-series-statistics',
  templateUrl: './experiment-time-series-statistics.component.html',
  styleUrls: ['./experiment-time-series-statistics.component.scss']
})
export class ExperimentTimeSeriesStatisticsComponent {

  public constructor(private experimentsService: ExperimentsService) {
  }

  public getClassifiersTimeSeriesStatisticsChartData(): (filter: TimeSeriesStatisticsFilter) => Observable<ChartDto> {
    return (filter: TimeSeriesStatisticsFilter) => this.experimentsService.getExperimentTimeSeriesStatistics(filter);
  }
}
