package com.eryckavel.dto.request;

import java.math.BigDecimal;

public record FluxoSaldoRequest(
    Long id,
    BigDecimal valor
) {
}
