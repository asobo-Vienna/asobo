import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminMediaList } from './admin-media-list';

describe('AdminMediaList', () => {
  let component: AdminMediaList;
  let fixture: ComponentFixture<AdminMediaList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminMediaList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminMediaList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
