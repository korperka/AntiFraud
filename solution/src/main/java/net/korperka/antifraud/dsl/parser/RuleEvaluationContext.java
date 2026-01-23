package net.korperka.antifraud.dsl.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.korperka.antifraud.dto.request.TransactionCreateRequest;
import net.korperka.antifraud.dto.response.UserResponse;
import net.korperka.antifraud.exception.InvalidFieldException;

import java.util.Optional;

@Data
@Builder
public class RuleEvaluationContext {
    private TransactionCreateRequest transaction;
    private UserResponse user;

    public Object getFieldValue(String fieldName) {
        if (transaction != null) {
            switch (fieldName) {
                case "amount": return transaction.getAmount();
                case "currency": return transaction.getCurrency();
                case "merchantId": return transaction.getMerchantId();
                case "ipAddress": return transaction.getIpAddress();
                case "deviceId": return transaction.getDeviceId();
            }
            if(transaction.getMetadata() != null && !transaction.getMetadata().isEmpty()) return transaction.getMetadata().get(fieldName);
        }
        if (user != null) {
            switch (fieldName) {
                case "user.id": return user.getId();
                case "user.age": return user.getAge();
                case "user.region": return user.getRegion();
                case "user.email": return user.getEmail();
                case "user.gender": return user.getGender();
                case "user.fullName": return user.getFullName();
                case "user.maritalStatus": return user.getMaritalStatus();
                case "user.role": return user.getRole();
                case "user.isActive": return user.isActive();
            }
        }

        return null;
    }
}