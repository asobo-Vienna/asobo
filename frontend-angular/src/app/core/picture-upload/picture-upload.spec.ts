import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PictureUpload } from './picture-upload';

describe('ProfilePictureUpload', () => {
  let component: PictureUpload;
  let fixture: ComponentFixture<PictureUpload>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PictureUpload]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PictureUpload);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
