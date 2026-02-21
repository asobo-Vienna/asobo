import { Injectable } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UrlUtilService {

  constructor(private route: ActivatedRoute) {}

  getParams(paramKeys: string[]): Map<string, string> {
    const paramMap = new Map<string, string>();
    this.route.paramMap.subscribe(params => {
      for (const key of paramKeys) {
        const value = params.get(key);
        if (value !== null) {
          paramMap.set(key, value);
        }
      }
    });
    return paramMap;
  }

  getParam(param: string): string | null {
    return this.getParams([param]).get(param) ?? null;
  }

  static getMediaUrl(relativePath: string) {
    const fileBaseUrl = environment.fileBaseUrl;
    console.log('getMediaUrl', relativePath.startsWith('/')
      ? `${fileBaseUrl}${relativePath}`
      : `${fileBaseUrl}/${relativePath}`);
    return relativePath.startsWith('/')
      ? `${fileBaseUrl}${relativePath}`
      : `${fileBaseUrl}/${relativePath}`;
  }

  static getUserRouterLink(username: string | undefined): string {
    if (!username) {
      return "";
    }
    return `${environment.userProfileBaseUrl}${username}`;
  }
}
