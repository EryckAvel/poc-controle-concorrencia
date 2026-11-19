package com.eryckavel.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table
public class Saldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private BigDecimal saldo;
    @Version
    private Integer version;

    public Saldo() {
    }

    public Saldo(Long id, BigDecimal saldo, Integer version) {
        this.id = id;
        this.saldo = saldo;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Saldo saldo1 = (Saldo) o;
        return Objects.equals(id, saldo1.id) && Objects.equals(nome, saldo1.nome) && Objects.equals(saldo, saldo1.saldo) && Objects.equals(version, saldo1.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, saldo, version);
    }
}
