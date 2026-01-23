package net.korperka.antifraud.specification;

import net.korperka.antifraud.entity.Transaction;
import net.korperka.antifraud.enums.TransactionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.criteria.Predicate;

public class TransactionSpecification {
    public static Specification<Transaction> filter(UUID userId, TransactionStatus status, Boolean fraud, LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null)
                predicates.add(cb.equal(root.get("userId"), userId));

            if (status != null)
                predicates.add(cb.equal(root.get("status"), status));

            if (fraud != null)
                predicates.add(cb.equal(root.get("fraud"), fraud));

            if (from != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), from));

            if (to != null)
                predicates.add(cb.lessThan(root.get("timestamp"), to));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
