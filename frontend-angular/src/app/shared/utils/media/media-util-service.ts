export class MediaUtilService {

  static isVideo(uri: string): boolean {
    return /\.(mp4|webm|ogg|ogv|mov|avi|mkv|m4v)$/i.test(uri);
  }
}
