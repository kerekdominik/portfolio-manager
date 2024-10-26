import { TestBed } from '@angular/core/testing';

import { CryptoPriceService } from './crypto-price.service';

describe('PriceService', () => {
  let service: CryptoPriceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CryptoPriceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
