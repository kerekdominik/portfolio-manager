import { TestBed } from '@angular/core/testing';

import { CryptoService } from './crypto.service';

describe('PriceService', () => {
  let service: CryptoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CryptoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
