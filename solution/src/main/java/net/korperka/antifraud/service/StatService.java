package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.response.*;
import net.korperka.antifraud.exception.DateFormatException;
import net.korperka.antifraud.projection.MerchantRiskRowProjection;
import net.korperka.antifraud.projection.RuleStatsProjection;
import net.korperka.antifraud.projection.StatsProjection;
import net.korperka.antifraud.projection.TimeSeriesProjection;
import net.korperka.antifraud.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatService {
    private final TransactionRepository transactionRepository;

    public MerchantRiskStats getMerchantRiskStats(
            LocalDateTime from,
            LocalDateTime to,
            String mcc,
            int top
    ) {
        if (to == null) to = LocalDateTime.now();
        if (from == null) from = to.minusDays(30);
        if(from.isAfter(to)) throw new DateFormatException();
        if (ChronoUnit.DAYS.between(from, to) > 90) throw new DateFormatException();

        List<MerchantRiskRowProjection> projections = transactionRepository.getMerchantRiskStats(from, to, mcc, top);

        List<MerchantRiskRow> items = projections.stream()
                .map(p -> new MerchantRiskRow(
                        p.getMerchantId(),
                        p.getMerchantCategoryCode(),
                        p.getTxCount(),
                        p.getGmv(),
                        p.getDeclineRate()
                ))
                .toList();

        return new MerchantRiskStats(items);
    }

    public RuleMatchStats getRuleStats(LocalDateTime from, LocalDateTime to, int limit) {
        if (to == null) to = LocalDateTime.now();
        if (from == null) from = to.minusDays(7);
        if(from.isAfter(to)) throw new DateFormatException();
        if (ChronoUnit.DAYS.between(from, to) > 90) throw new DateFormatException();

        List<RuleStatsProjection> stats = transactionRepository.getRuleStats(from, to, limit);

        List<RuleMatchRow> items = stats.stream()
                .map(s -> new RuleMatchRow(
                        UUID.fromString(s.getRuleId()),
                        s.getRuleName(),
                        s.getMatches(),
                        s.getUniqueUsers(),
                        s.getUniqueMerchants(),
                        s.getShareOfDeclines()
                ))
                .toList();

        return new RuleMatchStats(items);
    }

    public TransactionsTimeSeries getTimeSeries(
            LocalDateTime from,
            LocalDateTime to,
            String groupBy,
            String timezone,
            String channel
    ) {
        if (to == null) to = LocalDateTime.now();
        if (from == null) from = to.minusDays(7);
        if (groupBy == null) groupBy = "day";
        if(from.isAfter(to)) throw new DateFormatException();
        if (ChronoUnit.DAYS.between(from, to) > 90) throw new DateFormatException();

        List<TimeSeriesProjection> rows = transactionRepository.getTimeSeries(from, to, groupBy, channel);

        List<TransactionsTimePoint> points = rows.stream()
                .map(row -> new TransactionsTimePoint(
                        row.getBucket(),
                        row.getTxCount(),
                        row.getGmv(),
                        row.getApproved(),
                        row.getDeclined()
                ))
                .toList();

        return new TransactionsTimeSeries(points);
    }

    public StatsOverviewResponse getOverview(LocalDateTime from, LocalDateTime to) {
        if (to == null) to = LocalDateTime.now();
        if (from == null) from = LocalDateTime.now().minusDays(30);
        if(from.isAfter(to)) throw new DateFormatException();
        if (ChronoUnit.DAYS.between(from, to) > 90) throw new DateFormatException();

        StatsProjection stats = transactionRepository.getGeneralStats(from, to);

        long volume = stats.getVolume();
        double gmv = stats.getGmv();

        double approvalRate = 0.0;
        double declineRate = 0.0;

        if (volume > 0) {
            approvalRate = new BigDecimal((double) stats.getApprovedCount() / volume).setScale(2, RoundingMode.HALF_UP).doubleValue();
            declineRate = new BigDecimal((double) stats.getDeclinedCount() / volume).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }

        List<MerchantRiskRow> topMerchants = transactionRepository.getTopRiskMerchants(from, to);

        return StatsOverviewResponse.builder()
                .from(from)
                .to(to)
                .volume(volume)
                .gmv(gmv)
                .approvalRate(approvalRate)
                .declineRate(declineRate)
                .topRiskMerchants(topMerchants)
                .build();
    }
}