export class List<T> implements Iterable<T> {
  private items: T[] = [];

  constructor(items: T[]) {
    this.addAll(items);
  }

  [Symbol.iterator](): Iterator<T, any, any> {
    let index = 0;
    const items = this.items;

    return {
      next(): IteratorResult<T> {
        if (index < items.length) {
          return { value: items[index++], done: false };
        } else {
          return { value: undefined, done: true };
        }
      }
    };
  }

  // Add item
  add(item: T): void {
    this.items.push(item);
  }

  // Add multiple items
  addAll(items: T[]): void {
    this.items.push(...items);
  }

  // Get item by index
  get(index: number): T | undefined {
    return this.items[index];
  }

  // Remove by index
  removeAt(index: number): void {
    if (index >= 0 && index < this.items.length) {
      this.items.splice(index, 1);
    }
  }

  // Remove by value (requires equality check)
  remove(item: T): void {
    this.items = this.items.filter(i => i !== item);
  }

  // Check if contains item
  contains(item: T): boolean {
    return this.items.includes(item);
  }

  // Get size
  size(): number {
    return this.items.length;
  }

  // Clear list
  clear(): void {
    this.items = [];
  }

  // Return all items
  toArray(): T[] {
    return [...this.items];
  }
}
