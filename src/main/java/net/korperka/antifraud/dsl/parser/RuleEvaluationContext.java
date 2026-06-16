package net.korperka.antifraud.dsl.parser;

import lombok.Builder;
import lombok.Data;
import net.korperka.antifraud.dto.request.TransactionCreateRequest;
import net.korperka.antifraud.dto.response.UserResponse;

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
                case "merchantCategoryCode": return transaction.getMerchantCategoryCode();
                case "channel": return transaction.getChannel() != null ? transaction.getChannel().name() : null;
                case "location.country": return transaction.getLocation() != null ? transaction.getLocation().getCountry() : null;
                case "location.city": return transaction.getLocation() != null ? transaction.getLocation().getCity() : null;
                case "location.latitude": return transaction.getLocation() != null ? transaction.getLocation().getLatitude() : null;
                case "location.longitude": return transaction.getLocation() != null ? transaction.getLocation().getLongitude() : null;
            }
            if(transaction.getMetadata() != null && !transaction.getMetadata().isEmpty() && transaction.getMetadata().containsKey(fieldName)) return transaction.getMetadata().get(fieldName);
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