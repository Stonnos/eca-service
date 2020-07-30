export class CreateEditInstancesModel {
  id: number;
  tableName: string;
  file: File;

  constructor(id?: number, tableName?: string) {
    this.id = id;
    this.tableName = tableName;
  }
}
