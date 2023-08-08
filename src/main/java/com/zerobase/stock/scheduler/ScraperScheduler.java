package com.zerobase.stock.scheduler;

import com.zerobase.stock.model.Company;
import com.zerobase.stock.model.ScrapeResult;
import com.zerobase.stock.persist.CompanyRepository;
import com.zerobase.stock.persist.DividendRepository;
import com.zerobase.stock.persist.entity.CompanyEntity;
import com.zerobase.stock.persist.entity.DividendEntity;
import com.zerobase.stock.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor // 아래 private final로 선언해둔 레파지토리 등이 초기화 되도록 하기 위해서 작성 하는 것
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

    @Scheduled(fixedDelay = 1000)
    public void test1() throws InterruptedException {
        Thread.sleep(10000); // 10초간 일시정지
        System.out.println(Thread.currentThread().getName() + " -> 테스트 1: " + LocalDateTime.now());
    }

    @Scheduled(fixedDelay = 1000)
    public void test2() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " -> 테스트 2: " + LocalDateTime.now());
    }

    // 일정 주기마다 수행
     @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started.");

        // 저장된 회사 목록을 조회
        List<CompanyEntity> companyEntities = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for(var companyEntity : companyEntities) {
            log.info("scraping scheduler is started -> " + companyEntity.getName());
            ScrapeResult scrapeResult = yahooFinanceScraper.scrap(Company.builder()
                                                                        .name(companyEntity.getName())
                                                                        .ticker(companyEntity.getTicker())
                                                                        .build());

            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapeResult.getDividends().stream()
                    // 디비든 모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(companyEntity.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if(!exists) {
                            this.dividendRepository.save(e);
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000); // 3 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


    }
}
