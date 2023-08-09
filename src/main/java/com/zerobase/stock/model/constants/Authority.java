package com.zerobase.stock.model.constants;

public enum Authority {

    // spring에서 지원하는 기능을 쓰기 위해서는 앞에 prefix로 ROLE_을 붙여 주어야 한다.
    ROLE_READ,
    ROLE_WRITE;
}
