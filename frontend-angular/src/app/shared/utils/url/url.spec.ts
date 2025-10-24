import { TestBed } from '@angular/core/testing';
import { UrlUtilService } from './url-util-service';

describe('Url', () => {
  let service: UrlUtilService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UrlUtilService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
