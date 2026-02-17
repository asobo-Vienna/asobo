import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventBasicInfo } from './event-basic-info';

describe('EventInfo', () => {
  let component: EventBasicInfo;
  let fixture: ComponentFixture<EventBasicInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventBasicInfo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventBasicInfo);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
