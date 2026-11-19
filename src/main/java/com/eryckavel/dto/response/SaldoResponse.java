package com.eryckavel.dto.response;

import com.eryckavel.model.Saldo;

import java.math.BigDecimal;

public record SaldoResponse(Long id, String nome, BigDecimal saldo) {

    public SaldoResponse(Saldo saldo) {
        this(saldo.getId(), saldo.getNome(), saldo.getSaldo());
    }

}
