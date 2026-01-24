package net.korperka.antifraud.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StatsOverviewResponse {
    private LocalDateTime from;
    private LocalDateTime to;
    private long volume;
    private double gmv;
    private double approvalRate;
    private double declineRate;
    private List<MerchantRiskRow> topRiskMerchants;
}