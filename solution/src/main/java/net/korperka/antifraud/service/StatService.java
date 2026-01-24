package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.response.MerchantRiskRow;
import net.korperka.antifraud.dto.response.StatsOverviewResponse;
import net.korperka.antifraud.exception.DateFormatException;
import net.korperka.antifraud.projection.StatsProjection;
import net.korperka.antifraud.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatService {
    private final TransactionRepository transactionRepository;

    public StatsOverviewResponse getOverview(LocalDateTime from, LocalDateTime to) {
        if (to == null) to = LocalDateTime.now();
        if (from == null) from = to.minusDays(30);
        if(from.isAfter(to)) throw new DateFormatException();
        if (ChronoUnit.DAYS.between(from, to) > 90) throw new DateFormatException();

        StatsProjection stats = transactionRepository.getGeneralStats(from, to);

        long volume = stats.getVolume();
        double gmv = stats.getGmv();

        double approvalRate = 0.0;
        double declineRate = 0.0;

        if (volume > 0) {
            approvalRate = (double) stats.getApprovedCount() / volume;
            declineRate = (double) stats.getDeclinedCount() / volume;
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