import { TestBed } from '@angular/core/testing';

import { FormUtilService } from './form-util-service';

describe('FormUtilService', () => {
  let service: FormUtilService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FormUtilService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
