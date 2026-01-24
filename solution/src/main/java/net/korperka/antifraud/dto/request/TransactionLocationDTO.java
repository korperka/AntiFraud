package net.korperka.antifraud.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.korperka.antifraud.validation.ValidCoordinates;

@Data
@ValidCoordinates
public class TransactionLocationDTO {
    @Pattern(regexp = "^[A-Z]{2}$")
    private String country;

    @Size(max = 128)
    private String city;

    @Min(-90) @Max(90)
    private Double latitude;

    @Min(-180) @Max(180)
    private Double longitude;
}