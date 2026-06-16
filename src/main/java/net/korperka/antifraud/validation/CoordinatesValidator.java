package net.korperka.antifraud.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.korperka.antifraud.dto.request.TransactionLocationDTO;

public class CoordinatesValidator implements ConstraintValidator<ValidCoordinates, TransactionLocationDTO> {
    @Override
    public boolean isValid(TransactionLocationDTO dto, ConstraintValidatorContext context) {
        return dto == null || (dto.getLatitude() != null) == (dto.getLongitude() != null);
    }
}
