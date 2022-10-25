import { Component } from '@angular/core';
import { ChartDto} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ClassifiersService } from "../../classifiers/services/classifiers.service";

@Component({
  selector: 'app-classifiers-statistics',
  templateUrl: './classifiers-statistics.component.html',
  styleUrls: ['./classifiers-statistics.component.scss']
})
export class ClassifiersStatisticsComponent {

  public constructor(private classifiersService: ClassifiersService) {
  }

  public getClassifiersStatisticsChartData(): (from: string, to: string) => Observable<ChartDto> {
    return (from: string, to: string) => this.classifiersService.getClassifiersStatistics(from, to);
  }
}
