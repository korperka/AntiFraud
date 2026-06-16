package net.korperka.antifraud.projection;

import java.time.LocalDateTime;

public interface TimeSeriesProjection {
    LocalDateTime getBucket();
    long getTxCount();
    double getGmv();
    long getApproved();
    long getDeclined();
}
