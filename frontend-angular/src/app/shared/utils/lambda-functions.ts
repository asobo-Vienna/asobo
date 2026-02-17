export class LambdaFunctions {
  static compareById = (a: {id: string}, b: {id: string}) => a.id === b.id;

  static removeById<T extends { id: string }>(items: T[], id: string): T[] {
    return items.filter(item => item.id !== id);
  }
}


