package net.korperka.antifraud.repository;

import net.korperka.antifraud.entity.FraudRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FraudRuleRepository extends JpaRepository<FraudRule, Long> {
    boolean existsByName(String name);
    Optional<FraudRule> findById(UUID id);
}
