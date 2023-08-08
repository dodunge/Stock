package com.zerobase.stock.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrapeResult {

    private Company company;

    private List<Dividend> dividends;

    public ScrapeResult() {
        this.dividends = new ArrayList<>();
    }
}
