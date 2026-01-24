package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor @NoArgsConstructor
public class MerchantRiskRow {
    private String merchantId;
    private String merchantCategoryCode;
    private long txCount;
    private double gmv;
    private double declineRate;

    public MerchantRiskRow(
            String merchantId,
            String merchantCategoryCode,
            Long txCount,
            BigDecimal gmv,
            Double declineRate
    ) {
        this.merchantId = merchantId;
        this.merchantCategoryCode = merchantCategoryCode;
        this.txCount = txCount != null ? txCount : 0;

        this.gmv = gmv != null ? gmv.doubleValue() : 0.0;

        this.declineRate = declineRate != null ? declineRate : 0.0;
    }
}
