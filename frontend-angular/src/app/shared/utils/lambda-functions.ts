export class LambdaFunctions {
  static compareById = (a: any, b: any) => a.id === b.id;

  static removeById<T extends { id: any }>(items: T[], id: any): T[] {
    return items.filter(item => item.id !== id);
  }
}


