package com.eryckavel.service;

import com.eryckavel.dto.request.CadastroSaldoRequest;
import com.eryckavel.dto.request.FluxoSaldoRequest;
import com.eryckavel.dto.request.TransferenciaRequest;
import com.eryckavel.dto.response.SaldoResponse;
import com.eryckavel.model.Saldo;
import com.eryckavel.repository.SaldoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaldoService {

    private static final Logger log = LoggerFactory.getLogger(SaldoService.class);

    private final SaldoRepository saldoRepository;

    public SaldoService(SaldoRepository saldoRepository) {
        this.saldoRepository = saldoRepository;
    }

    public SaldoResponse criarSaldo(CadastroSaldoRequest cadastroSaldoRequest) {
        Saldo saldo = new Saldo();
        saldo.setNome(cadastroSaldoRequest.nome());
        saldo.setSaldo(cadastroSaldoRequest.saldoInicial());
        saldoRepository.save(saldo);
        return new SaldoResponse(saldo);
    }

    @Transactional
    public SaldoResponse depositar(FluxoSaldoRequest request) {
        Saldo saldo = saldoRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Saldo não encontrado!"));
        saldo.setSaldo(saldo.getSaldo().add(request.valor()));
        log.info("Deposito processado - novo saldo: {}", saldo.getSaldo());
        saldoRepository.save(saldo);
        return new SaldoResponse(saldo);
    }

    @Transactional
    public SaldoResponse sacar(FluxoSaldoRequest request) {
        Saldo saldo = saldoRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Saldo não encontrado!"));
        if (saldo.getSaldo().compareTo(request.valor()) < 0) throw new IllegalArgumentException("Saldo insuficiente!");
        saldo.setSaldo(saldo.getSaldo().subtract(request.valor()));
        log.info("Saque processado - novo saldo: {}", saldo.getSaldo());
        saldoRepository.save(saldo);
        return new SaldoResponse(saldo);
    }

    @Transactional
    public SaldoResponse depositarJQPL(FluxoSaldoRequest request) {
        Saldo saldo = saldoRepository.consultarSaldoPorIdJPQL(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Saldo não encontrado!"));
        saldo.setSaldo(saldo.getSaldo().add(request.valor()));
        log.info("Deposito processado - novo saldo: {}", saldo.getSaldo());
        saldoRepository.save(saldo);
        return new SaldoResponse(saldo);
    }

    @Transactional
    public SaldoResponse sacarNative(FluxoSaldoRequest request) {
        Saldo saldo = saldoRepository.consultarSaldoPorIdNativo(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Saldo não encontrado!"));
        if (saldo.getSaldo().compareTo(request.valor()) < 0) throw new IllegalArgumentException("Saldo insuficiente!");
        saldo.setSaldo(saldo.getSaldo().subtract(request.valor()));
        log.info("Saque processado - novo saldo: {}", saldo.getSaldo());
        saldoRepository.save(saldo);
        return new SaldoResponse(saldo);
    }

    @Retryable(
            includes = PessimisticLockingFailureException.class,
            maxRetries = 3,
            delay = 100,
            multiplier = 2
    )
    @Transactional
    public List<SaldoResponse> transferirSaldo(TransferenciaRequest request) throws InterruptedException {
        log.info("Tentativa de transferência {} → {}", request.idOrigem(), request.idDestino());

        Saldo origem = saldoRepository.consultarSaldoPorIdJPQL(request.idOrigem())
                .orElseThrow(() -> new EntityNotFoundException("Origem não encontrada!"));

        Thread.sleep(200);

        Saldo destino = saldoRepository.consultarSaldoPorIdNativo(request.idDestino())
                .orElseThrow(() -> new EntityNotFoundException("Destino não encontrado!"));

        origem.setSaldo(origem.getSaldo().subtract(request.valor()));
        destino.setSaldo(destino.getSaldo().add(request.valor()));
        List<Saldo> saldosAtualizados = new ArrayList<>();
        saldosAtualizados.add(origem);
        saldosAtualizados.add(destino);
        return saldosAtualizados.stream().map(SaldoResponse::new).collect(Collectors.toList());
    }

}
