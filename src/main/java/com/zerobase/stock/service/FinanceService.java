package com.zerobase.stock.service;

import com.zerobase.stock.exception.impl.NoCompanyException;
import com.zerobase.stock.model.Company;
import com.zerobase.stock.model.Dividend;
import com.zerobase.stock.model.ScrapeResult;
import com.zerobase.stock.model.constants.CacheKey;
import com.zerobase.stock.persist.CompanyRepository;
import com.zerobase.stock.persist.DividendRepository;
import com.zerobase.stock.persist.entity.CompanyEntity;
import com.zerobase.stock.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 요청이 자주 들어오는가? 특정 회사의 요청이 아주 자주 들어옴
    // 자주 변경되는 데이터 인가? 과거의 배당금이 변할 일도 없고, 회사명이 바뀌기는 쉽지 않다. 짧으면 한달 길면 일년에 한번 배당금이 추가되는 정도에 변화가 있을 것으로 예상
    // -> 캐시 서버를 사용하면 좋겠다는 결론이 남.
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE) // redis의 key-value와는 다른것
    public ScrapeResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity companyEntity = this.companyRepository.findByName(companyName)
                                            .orElseThrow(() -> new NoCompanyException());

        // 2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(companyEntity.getId());

        // 3. 결과 조합 후 반환
//        List<Dividend> dividends = new ArrayList<>();
//        for(var entity : dividendEntities) {
//            dividends.add(Dividend.builder()
//                                .date(entity.getDate())
//                                .dividend(entity.getDividend())
//                                .build());
//        }

        List<Dividend> dividends = dividendEntities.stream()
                                                    .map(e -> Dividend.builder()
                                                            .date(e.getDate())
                                                            .dividend(e.getDividend())
                                                            .build())
                                                    .collect(Collectors.toList());

        return new ScrapeResult(Company.builder()
                                        .name(companyEntity.getName())
                                        .ticker(companyEntity.getTicker())
                                        .build(),
                                dividends);
    }
}
