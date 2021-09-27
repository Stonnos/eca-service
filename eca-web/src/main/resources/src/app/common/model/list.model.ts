export class ListModel<T> {
  label: string;
  value: T;

  public constructor(label: string, value: T) {
    this.label = label;
    this.value = value;
  }
}
