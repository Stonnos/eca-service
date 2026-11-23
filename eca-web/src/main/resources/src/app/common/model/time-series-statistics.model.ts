export enum TimePeriod {
  CUSTOM = 'CUSTOM',
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
  period: TimePeriod;
  from?: string;
  to?: string;
  groupBy: TimePeriodGroupBy;
}
