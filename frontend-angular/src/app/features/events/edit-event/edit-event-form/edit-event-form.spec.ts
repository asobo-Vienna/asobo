import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditEventForm } from './edit-event-form';

describe('EditEventForm', () => {
  let component: EditEventForm;
  let fixture: ComponentFixture<EditEventForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditEventForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditEventForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
