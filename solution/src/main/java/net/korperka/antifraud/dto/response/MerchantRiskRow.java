package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantRiskRow {
    private String merchantId;
    private String merchantCategoryCode;
    private long txCount;
    private double gmv;
    private double declineRate;
}
