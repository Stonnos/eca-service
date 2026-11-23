import { Component } from '@angular/core';
import { ChartDto} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ClassifiersService } from "../../classifiers/services/classifiers.service";
import { TimeSeriesStatisticsFilter } from '../../common/model/time-series-statistics.model';

@Component({
  selector: 'app-classifiers-time-series-statistics',
  templateUrl: './classifiers-time-series-statistics.component.html',
  styleUrls: ['./classifiers-time-series-statistics.component.scss']
})
export class ClassifiersTimeSeriesStatisticsComponent {

  public constructor(private classifiersService: ClassifiersService) {
  }

  public getClassifiersTimeSeriesStatisticsChartData(): (filter: TimeSeriesStatisticsFilter) => Observable<ChartDto> {
    return (filter: TimeSeriesStatisticsFilter) => this.classifiersService.getClassifiersTimeSeriesStatistics(filter);
  }
}
