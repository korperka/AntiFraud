package net.korperka.antifraud.repository;

import net.korperka.antifraud.dto.response.MerchantRiskRow;
import net.korperka.antifraud.dto.response.TransactionsTimeSeries;
import net.korperka.antifraud.entity.Transaction;
import net.korperka.antifraud.projection.StatsProjection;
import net.korperka.antifraud.projection.TimeSeriesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    @Query(value = """
    SELECT 
        date_trunc(:groupBy, t.timestamp) as bucket,
        COUNT(*) as txCount,
        COALESCE(SUM(t.amount), 0) as gmv,
        SUM(CASE WHEN t.status = 'APPROVED' THEN 1 ELSE 0 END) as approved,
        SUM(CASE WHEN t.status = 'DECLINED' THEN 1 ELSE 0 END) as declined
    FROM transactions t
    WHERE t.timestamp >= :from 
      AND t.timestamp < :to
      AND (:channel IS NULL OR t.channel = :channel)
    GROUP BY bucket
    ORDER BY bucket ASC
    """, nativeQuery = true)
    List<TimeSeriesProjection> getTimeSeries(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("groupBy") String groupBy,
            @Param("channel") String channel
    );

    @Query("SELECT " +
            "COUNT(t) as volume, " +
            "COALESCE(SUM(t.amount), 0) as gmv, " +
            "SUM(CASE WHEN t.status = 'APPROVED' THEN 1 ELSE 0 END) as approvedCount, " +
            "SUM(CASE WHEN t.status = 'DECLINED' THEN 1 ELSE 0 END) as declinedCount " +
            "FROM Transaction t " +
            "WHERE t.timestamp >= :from AND t.timestamp < :to")
    StatsProjection getGeneralStats(LocalDateTime from, LocalDateTime to);

    @Query("SELECT new net.korperka.antifraud.dto.response.MerchantRiskRow(" +
            "t.merchantId, " +
            "MAX(t.merchantCategoryCode), " +
            "COUNT(t), " +
            "COALESCE(SUM(t.amount), 0), " +
            "(CAST(SUM(CASE WHEN t.status = 'DECLINED' THEN 1 ELSE 0 END) AS double) / COUNT(t)) " +
            ") " +
            "FROM Transaction t " +
            "WHERE t.timestamp >= :from AND t.timestamp < :to " +
            "GROUP BY t.merchantId " +
            "ORDER BY (CAST(SUM(CASE WHEN t.status = 'DECLINED' THEN 1 ELSE 0 END) AS double) / COUNT(t)) DESC, COUNT(t) DESC " +
            "LIMIT 10")
    List<MerchantRiskRow> getTopRiskMerchants(LocalDateTime from, LocalDateTime to);
    Optional<Transaction> findById(UUID id);
}
