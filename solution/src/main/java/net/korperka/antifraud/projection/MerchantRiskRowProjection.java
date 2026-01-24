package net.korperka.antifraud.projection;

public interface MerchantRiskRowProjection {
    String getMerchantId();
    String getMerchantCategoryCode();
    Long getTxCount();
    Double getGmv();
    Double getDeclineRate();
}