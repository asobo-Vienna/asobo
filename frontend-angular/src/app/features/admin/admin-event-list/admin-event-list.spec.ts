import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminEventList } from './admin-event-list';

describe('AdminEventList', () => {
  let component: AdminEventList;
  let fixture: ComponentFixture<AdminEventList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminEventList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminEventList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
