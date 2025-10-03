class List<T> {
  private items: T[] = [];

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
