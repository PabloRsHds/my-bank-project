import { AnimateOnScrollDirective } from './animate-on-scroll.directive';
import { ElementRef, Renderer2 } from '@angular/core';

describe('AnimateOnScrollDirective', () => {
  let elementRefMock: ElementRef;
  let rendererMock: Renderer2;

  beforeEach(() => {
    elementRefMock = new ElementRef(document.createElement('div'));
    rendererMock = jasmine.createSpyObj('Renderer2', ['addClass', 'removeClass']);
  });

  it('should create an instance', () => {
    const directive = new AnimateOnScrollDirective(elementRefMock, rendererMock);
    expect(directive).toBeTruthy();
  });
});
