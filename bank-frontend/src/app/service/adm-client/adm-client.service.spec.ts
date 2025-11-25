import { TestBed } from '@angular/core/testing';
import { AdmClientService } from './adm-client.service';

describe('AdmClientService', () => {
  let service: AdmClientService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdmClientService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
