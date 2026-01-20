package net.korperka.antifraud.repository;

import net.korperka.antifraud.entity.FraudRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudRuleRepository extends JpaRepository<FraudRule, Long> {
    boolean existsByName(String name);
}
