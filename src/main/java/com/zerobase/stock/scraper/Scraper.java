package com.zerobase.stock.scraper;

import com.zerobase.stock.model.Company;
import com.zerobase.stock.model.ScrapeResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapeResult scrap(Company company);
}
