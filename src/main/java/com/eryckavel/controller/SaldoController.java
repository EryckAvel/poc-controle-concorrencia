package com.eryckavel.controller;

import com.eryckavel.dto.request.CadastroSaldoRequest;
import com.eryckavel.dto.request.FluxoSaldoRequest;
import com.eryckavel.dto.request.TransferenciaRequest;
import com.eryckavel.dto.response.SaldoResponse;
import com.eryckavel.service.SaldoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saldo")
public class SaldoController {

    private final SaldoService service;

    public SaldoController(SaldoService service) {
        this.service = service;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<SaldoResponse> cadastrarSaldo(@RequestBody CadastroSaldoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarSaldo(request));
    }

    @PutMapping("/depositar")
    public ResponseEntity<SaldoResponse> depositarSaldo(@RequestBody FluxoSaldoRequest request) {
        return ResponseEntity.ok(service.depositar(request));
    }

    @PutMapping("/sacar")
    public ResponseEntity<SaldoResponse> sacarSaldo(@RequestBody FluxoSaldoRequest request) {
        return ResponseEntity.ok(service.sacar(request));
    }

    @PutMapping("/depositar/jpql")
    public ResponseEntity<SaldoResponse> depositarSaldoJPQL(@RequestBody FluxoSaldoRequest request) {
        return ResponseEntity.ok(service.depositarJQPL(request));
    }

    @PutMapping("/sacar/native")
    public ResponseEntity<SaldoResponse> sacarSaldoNative(@RequestBody FluxoSaldoRequest request) {
        return ResponseEntity.ok(service.sacarNative(request));
    }

    @PostMapping("/transferir")
    public ResponseEntity<List<SaldoResponse>> transferirSaldo(@RequestBody TransferenciaRequest request) throws InterruptedException {
        return ResponseEntity.ok(service.transferirSaldo(request));
    }

}
