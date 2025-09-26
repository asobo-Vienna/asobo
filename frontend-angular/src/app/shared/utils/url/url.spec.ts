import { TestBed } from '@angular/core/testing';

import { Url } from './url-util-service';

describe('Url', () => {
  let service: Url;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Url);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
