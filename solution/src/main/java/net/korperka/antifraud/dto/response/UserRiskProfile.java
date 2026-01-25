package net.korperka.antifraud.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserRiskProfile {
    private UUID userId;

    private long txCount_24h;
    private double gmv_24h;

    private long distinctDevices_24h;
    private long distinctIps_24h;
    private long distinctCities_24h;

    private double declineRate_30d;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime lastSeenAt;
}