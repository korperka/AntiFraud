package net.korperka.antifraud.repository;

import net.korperka.antifraud.dto.response.MerchantRiskRow;
import net.korperka.antifraud.dto.response.TransactionsTimeSeries;
import net.korperka.antifraud.entity.Transaction;
import net.korperka.antifraud.projection.*;
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
            COUNT(*) FILTER (WHERE t.timestamp >= :since24h) as txCount24h,
            COALESCE(SUM(t.amount) FILTER (WHERE t.timestamp >= :since24h), 0) as gmv24h,
            COUNT(DISTINCT t.device_id) FILTER (WHERE t.timestamp >= :since24h) as distinctDevices24h,
            COUNT(DISTINCT t.ip_address) FILTER (WHERE t.timestamp >= :since24h) as distinctIps24h,
            COUNT(DISTINCT t.city) FILTER (WHERE t.timestamp >= :since24h) as distinctCities24h,
            COUNT(*) FILTER (WHERE t.timestamp >= :since30d) as txCount30d,
            COUNT(*) FILTER (WHERE t.timestamp >= :since30d AND t.status = 'DECLINED') as declinedCount30d,
            
            MAX(t.timestamp) as lastSeenAt
        FROM transactions t
        WHERE t.user_id = :userId
    """, nativeQuery = true)
    UserRiskStatsProjection getUserRiskStats(
            @Param("userId") UUID userId,
            @Param("since24h") LocalDateTime since24h,
            @Param("since30d") LocalDateTime since30d
    );

    @Query(value = """
    SELECT 
        t.merchant_id as merchantId, 
        MAX(t.merchant_category_code) as merchantCategoryCode, 
        COUNT(*) as txCount, 
        COALESCE(SUM(t.amount), 0) as gmv, 
        (CAST(SUM(CASE WHEN t.status = 'DECLINED' THEN 1 ELSE 0 END) AS float) / NULLIF(COUNT(*), 0)) as declineRate 
    FROM transactions t 
    WHERE t.timestamp >= :from AND t.timestamp < :to 
      AND (:mcc IS NULL OR t.merchant_category_code = :mcc)
    GROUP BY t.merchant_id 
    ORDER BY declineRate DESC, txCount DESC 
    LIMIT :limit
    """, nativeQuery = true)
    List<MerchantRiskRowProjection> getMerchantRiskStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("mcc") String mcc,
            @Param("limit") int limit
    );

    @Query(value = """
    WITH exploded_rules AS (
        SELECT 
            t.user_id, 
            t.merchant_id, 
            t.status,
            r->>'ruleId' as rule_id,
            r->>'ruleName' as rule_name
        FROM transactions t,
             jsonb_array_elements(t.rule_results) as r
        WHERE t.timestamp >= :from AND t.timestamp < :to
          AND t.rule_results IS NOT NULL
          AND (r->>'matched') = 'true'
    ),
    total_declined AS (
        SELECT COUNT(*) as cnt FROM transactions t
        WHERE t.timestamp >= :from AND t.timestamp < :to 
          AND t.status = 'DECLINED'
    )
    SELECT 
        er.rule_id as ruleId,
        er.rule_name as ruleName,
        COUNT(*) as matches,
        COUNT(DISTINCT er.user_id) as uniqueUsers,
        COUNT(DISTINCT er.merchant_id) as uniqueMerchants,
        COALESCE((CAST(COUNT(*) AS float) / NULLIF((SELECT cnt FROM total_declined), 0)), 0) as shareOfDeclines
    FROM exploded_rules er
    GROUP BY er.rule_id, er.rule_name
    ORDER BY matches DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<RuleStatsProjection> getRuleStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("limit") int limit
    );

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
