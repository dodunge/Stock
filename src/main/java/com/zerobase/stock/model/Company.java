package com.zerobase.stock.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Company {

    private String ticker;
    private String name;
}
