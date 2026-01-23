package net.korperka.antifraud.repository;

import net.korperka.antifraud.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean findById(UUID id);
}
