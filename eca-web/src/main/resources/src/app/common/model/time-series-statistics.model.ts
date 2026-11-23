export enum TimePeriod {
  DAY = 'DAY',
  MONTH = 'MONTH',
  THREE_MONTHS = 'THREE_MONTHS',
  SIX_MONTHS = 'SIX_MONTHS',
  YEAR = 'YEAR'
}

export enum TimePeriodGroupBy {
  DAY = 'DAY',
  MONTH = 'MONTH'
}

export class TimeSeriesStatisticsFilter {
  period?: TimePeriod;
  day?: string;
  groupBy: TimePeriodGroupBy;
}
