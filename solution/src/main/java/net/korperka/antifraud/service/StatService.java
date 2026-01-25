package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.response.*;
import net.korperka.antifraud.entity.User;
import net.korperka.antifraud.enums.Role;
import net.korperka.antifraud.exception.DateFormatException;
import net.korperka.antifraud.exception.NotFoundException;
import net.korperka.antifraud.projection.*;
import net.korperka.antifraud.repository.FraudRuleRepository;
import net.korperka.antifraud.repository.TransactionRepository;
import net.korperka.antifraud.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
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
    private final FraudRuleRepository ruleRepository;
    private final UserRepository userRepository;

    public UserRiskProfile getUserRiskProfile(UUID sourceId, UUID targetId) {
        if (!userRepository.existsById(targetId)) throw new NotFoundException(targetId);

        User source = userRepository.findById(sourceId).orElseThrow(() -> new NotFoundException(sourceId));
        if(source.getRole() != Role.ADMIN && !sourceId.equals(targetId)) throw new AccessDeniedException("Forbidden");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since24h = now.minusHours(24);
        LocalDateTime since30d = now.minusDays(30);

        UserRiskStatsProjection stats = transactionRepository.getUserRiskStats(targetId, since24h, since30d);

        double declineRate30d = 0.0;
        long total30d = stats.getTxCount30d() != null ? stats.getTxCount30d() : 0;
        long declined30d = stats.getDeclinedCount30d() != null ? stats.getDeclinedCount30d() : 0;

        if (total30d > 0) {
            declineRate30d = new BigDecimal((double) declined30d / total30d)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return UserRiskProfile.builder()
                .userId(targetId)
                .txCount_24h(stats.getTxCount24h() != null ? stats.getTxCount24h() : 0)
                .gmv_24h(stats.getGmv24h() != null ? stats.getGmv24h() : 0.0)
                .distinctDevices_24h(stats.getDistinctDevices24h() != null ? stats.getDistinctDevices24h() : 0)
                .distinctIps_24h(stats.getDistinctIps24h() != null ? stats.getDistinctIps24h() : 0)
                .distinctCities_24h(stats.getDistinctCities24h() != null ? stats.getDistinctCities24h() : 0)
                .declineRate_30d(declineRate30d)
                .lastSeenAt(stats.getLastSeenAt())
                .build();
    }

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