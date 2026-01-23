package net.korperka.antifraud.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLocation {
    private String country;
    private String city;
    private Double latitude;
    private Double longitude;
}