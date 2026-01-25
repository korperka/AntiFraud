package net.korperka.antifraud.projection;

import java.time.LocalDateTime;

public interface UserRiskStatsProjection {
    Long getTxCount24h();
    Double getGmv24h();
    Long getDistinctDevices24h();
    Long getDistinctIps24h();
    Long getDistinctCities24h();

    Long getTxCount30d();
    Long getDeclinedCount30d();

    LocalDateTime getLastSeenAt();
}