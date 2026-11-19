package com.eryckavel.repository;

import com.eryckavel.model.Saldo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SaldoRepository extends JpaRepository<Saldo, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Saldo s WHERE s.id=:id")
    Optional<Saldo> consultarSaldoPorIdJPQL(Long id);

    @Query(value = "SELECT * FROM saldo WHERE id=:id FOR UPDATE", nativeQuery = true)
    Optional<Saldo> consultarSaldoPorIdNativo(Long id);

}
