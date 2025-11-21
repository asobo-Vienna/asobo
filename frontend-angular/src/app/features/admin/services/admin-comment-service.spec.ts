import { TestBed } from '@angular/core/testing';

import { AdminCommentService } from './admin-comment-service';

describe('AdminCommentService', () => {
  let service: AdminCommentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminCommentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
