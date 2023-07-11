export class CreateEditInstancesModel {
  id: number;
  relationName: string;
  file: File;

  constructor(id?: number, relationName?: string) {
    this.id = id;
    this.relationName = relationName;
  }
}
