package com.eryckavel.dto.request;

import java.math.BigDecimal;

public record TransferenciaRequest(
        Long idOrigem,
        Long idDestino,
        BigDecimal valor
) {
}
