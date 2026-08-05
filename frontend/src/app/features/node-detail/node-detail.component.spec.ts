import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NodeDetailComponent } from './node-detail.component';

describe('NodeDetailComponent', () => {
  let component: NodeDetailComponent;
  let fixture: ComponentFixture<NodeDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NodeDetailComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(NodeDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
