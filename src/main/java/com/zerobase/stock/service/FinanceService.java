package com.zerobase.stock.service;

import com.zerobase.stock.model.Company;
import com.zerobase.stock.model.Dividend;
import com.zerobase.stock.model.ScrapeResult;
import com.zerobase.stock.persist.CompanyRepository;
import com.zerobase.stock.persist.DividendRepository;
import com.zerobase.stock.persist.entity.CompanyEntity;
import com.zerobase.stock.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapeResult getDividendByCompanyName(String companyName) {
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity companyEntity = this.companyRepository.findByName(companyName)
                                            .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

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
