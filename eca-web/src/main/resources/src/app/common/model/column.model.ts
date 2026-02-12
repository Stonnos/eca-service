export class ColumnModel {
  name: string;
  label: string;
  sortBy?: string;

  public constructor(name?: string, label?: string) {
    this.name = name;
    this.label = label;
  }
}
