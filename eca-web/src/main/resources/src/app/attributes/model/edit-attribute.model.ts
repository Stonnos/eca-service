export class EditAttributeModel {
  id: number;
  selected: boolean;

  public constructor(id: number, selected: boolean) {
    this.id = id;
    this.selected = selected;
  }
}
