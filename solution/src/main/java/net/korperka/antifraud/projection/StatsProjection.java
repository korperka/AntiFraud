package net.korperka.antifraud.projection;

public interface StatsProjection {
    long getVolume();
    double getGmv();
    long getApprovedCount();
    long getDeclinedCount();
}