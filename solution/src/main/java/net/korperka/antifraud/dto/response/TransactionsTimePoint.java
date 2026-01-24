package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class TransactionsTimePoint {
    private LocalDateTime bucketStart;
    private long txCount;
    private double gmv;
    private double approvalRate;
    private double declineRate;

    public TransactionsTimePoint(LocalDateTime bucketStart, long txCount, double gmv, long approved, long declined) {
        this.bucketStart = bucketStart;
        this.txCount = txCount;
        this.gmv = gmv;
        this.approvalRate = txCount == 0 ? 0 : (double) approved / txCount;
        this.declineRate = txCount == 0 ? 0 : (double) declined / txCount;
    }
}