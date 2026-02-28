export function getTextPreview(text: string, length: number): string {
  if (text.length > length) {
    return `${text.substring(0, length - 1)}...`;
  }
  return text;
}
