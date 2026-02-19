import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventAdmins } from './event-admins';

describe('EventAdmins', () => {
  let component: EventAdmins;
  let fixture: ComponentFixture<EventAdmins>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventAdmins]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventAdmins);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
