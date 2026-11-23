import { Component, Input, OnInit } from '@angular/core';
import { MessageService, SelectItem } from 'primeng/api';
import { TimePeriod, TimePeriodGroupBy, TimeSeriesStatisticsFilter } from '../model/time-series-statistics.model';
import { ChartDataDto, ChartDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from 'rxjs/internal/Observable';
import { DatePipe } from '@angular/common';
import { finalize } from 'rxjs/internal/operators';

@Component({
  selector: 'app-time-series-statistics',
  templateUrl: './time-series-statistics.component.html',
  styleUrls: ['./time-series-statistics.component.scss']
})
export class TimeSeriesStatisticsComponent implements OnInit {
  public loading = false;
  public now: Date = new Date();

  public periodOptions: SelectItem[] = [
    { label: '1 день', value: TimePeriod.DAY },
    { label: 'Месяц', value: TimePeriod.MONTH },
    { label: '3 месяца', value: TimePeriod.THREE_MONTHS },
    { label: '6 месяцев', value: TimePeriod.SIX_MONTHS },
    { label: '1 год', value: TimePeriod.YEAR }
  ];

  public selectedPeriod: TimePeriod = TimePeriod.MONTH;
  public selectedSingleDate: Date = new Date();

  public groupOptions: SelectItem[] = [
    { label: 'По дням', value: TimePeriodGroupBy.DAY, disabled: false },
    { label: 'По месяцам', value: TimePeriodGroupBy.MONTH, disabled: true }
  ];
  public selectedGroup: TimePeriodGroupBy = TimePeriodGroupBy.DAY;

  public dataSet: any;
  public barOptions: any;
  public total = 0;

  private datePipe: DatePipe = new DatePipe("en-US");

  private dateFormat: string = "yyyy-MM-dd";

  @Input()
  public chartTitle: string;
  @Input()
  public totalCountTitle: string;
  @Input()
  public loadData: (filer: TimeSeriesStatisticsFilter) => Observable<ChartDto>;

  public constructor(private messageService: MessageService) {
  }

  public ngOnInit() {
    this.initChartOptions();
    this.onPeriodChange();
    this.onShow();
  }

  public onPeriodChange(): void {
    const isSingleDayOrMonth = this.selectedPeriod === TimePeriod.DAY || this.selectedPeriod === TimePeriod.MONTH;

    if (isSingleDayOrMonth) {
      // Allow grouping by day only for 1 day and 1 month
      this.selectedGroup = TimePeriodGroupBy.DAY;
      this.groupOptions = [
        { label: 'По дням', value: TimePeriodGroupBy.DAY, disabled: false },
        { label: 'По месяцам', value: TimePeriodGroupBy.MONTH, disabled: true }
      ];
    } else {
      // Allow both grouping options for 3–12 months
      this.groupOptions = [
        { label: 'По дням', value: TimePeriodGroupBy.DAY, disabled: false },
        { label: 'По месяцам', value: TimePeriodGroupBy.MONTH, disabled: false }
      ];
    }
  }

  public onShow() {
    const filter = this.buildFilter();
    console.log('TimeSeriesStatisticsFilter:', filter);
    this.loading = true;
    this.loadData(filter)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (chartData: ChartDto) => {
          this.total = chartData.total;
          this.dataSet = {
            labels: chartData.dataItems.map((chartData: ChartDataDto) => this.formatLabel(chartData.label, filter.groupBy)),
            datasets: [
              {
                backgroundColor: '#a51d36',
                borderColor: '#a5a5a5',
                data: chartData.dataItems.map((chartData: ChartDataDto) => chartData.count)
              }
            ]
          };
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });

  }

  private buildFilter(): TimeSeriesStatisticsFilter {
    const filter: TimeSeriesStatisticsFilter = new TimeSeriesStatisticsFilter();
    filter.groupBy = this.selectedGroup;
    filter.period = this.selectedPeriod;
    if (this.selectedPeriod === TimePeriod.DAY) {
      if (!this.selectedSingleDate) {
        this.selectedSingleDate = new Date();
      }
      filter.day = this.transformDate(this.selectedSingleDate);
    }
    return filter;
  }

  private formatLabel(rawLabel: string, groupBy: TimePeriodGroupBy): string {
    if (!rawLabel) {
      return '';
    }
    const parts = rawLabel.split('-').map(Number);
    if (groupBy === TimePeriodGroupBy.MONTH && parts.length >= 2) {
      const [year, month] = parts;
      const date = new Date(year, month - 1, 1);
      return new Intl.DateTimeFormat('ru-RU', {
        month: 'short',
        year: 'numeric'
      })
        .format(date)
        .replace(' г.', '');
    }
    if (groupBy === TimePeriodGroupBy.DAY && parts.length === 3) {
      const [year, month, day] = parts;
      const date = new Date(year, month - 1, day);

      return new Intl.DateTimeFormat('ru-RU', {
        day: 'numeric',
        month: 'short',
        year: 'numeric'
      })
        .format(date)
        .replace(' г.', '');
    }
    return rawLabel;
  }

  private transformDate(date: Date): string {
    return date ? this.datePipe.transform(date, this.dateFormat) : '';
  }

  private initChartOptions() {
    this.barOptions = {
      title: {
        display: true,
        text: this.chartTitle,
        fontSize: 20
      },
      legend: {
        display: false
      },
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        xAxes: [{
          ticks: {
            autoSkip: true,
            maxTicksLimit: 12
          }
        }],
        yAxes: [{
          ticks: {
            beginAtZero: true,
            precision: 0
          }
        }]
      }
    };
  }
}
