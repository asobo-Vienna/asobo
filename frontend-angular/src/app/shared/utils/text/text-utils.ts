export function getTextPreview(text: string, length: number): string {
  if (text.length > length) {
    return `${text.substring(0, length - 1)}...`;
  }
  return text;
}

function capitalizeFirstLetter(text: string): string {
  return text.charAt(0).toUpperCase() + text.slice(1);
}
