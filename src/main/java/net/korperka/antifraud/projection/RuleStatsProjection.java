package net.korperka.antifraud.projection;

public interface RuleStatsProjection {
    String getRuleId();
    String getRuleName();
    long getMatches();
    long getUniqueUsers();
    long getUniqueMerchants();
    double getShareOfDeclines();
}
