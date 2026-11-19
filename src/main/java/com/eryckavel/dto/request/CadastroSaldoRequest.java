package com.eryckavel.dto.request;

import java.math.BigDecimal;

public record CadastroSaldoRequest(String nome, BigDecimal saldoInicial) {
}
