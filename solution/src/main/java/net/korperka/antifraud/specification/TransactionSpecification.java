package net.korperka.antifraud.specification;

import net.korperka.antifraud.entity.Transaction;
import net.korperka.antifraud.enums.TransactionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionSpecification {
    public static Specification<Transaction> filter(UUID userId, TransactionStatus status, Boolean fraud, LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            Specification<Transaction> spec = Specification.where(null);

            if (userId != null)
                spec = spec.and((r, q, b) -> b.equal(r.get("userId"), userId));

            if (status != null)
                spec = spec.and((r, q, b) -> b.equal(r.get("status"), status));

            if (fraud != null)
                spec = spec.and((r, q, b) -> b.equal(r.get("fraud"), fraud));

            if (from != null)
                spec = spec.and((r, q, b) -> b.greaterThanOrEqualTo(r.get("createdAt"), from));

            if (to != null)
                spec = spec.and((r, q, b) -> b.lessThanOrEqualTo(r.get("createdAt"), to));

            return spec.toPredicate(root, query, cb);
        };
    }
}
