package net.korperka.antifraud.dsl.node;

import lombok.AllArgsConstructor;
import net.korperka.antifraud.dsl.parser.RuleEvaluationContext;
import net.korperka.antifraud.dsl.token.TokenType;
import net.korperka.antifraud.exception.InvalidFieldException;
import net.korperka.antifraud.exception.InvalidOperatorException;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ComparisonNode implements Node {
    private final String field;
    private final String operator;
    private final String expectedValue;
    private final TokenType valueType;

    private static final Set<String> ALLOW_NUMBER_COMPARISON = Set.of(
            "merchantCategoryCode", "channel", "merchantId", "user.id"
    );

    private static final Map<String, TokenType> FIELD_TYPES = Map.ofEntries(
            Map.entry("amount", TokenType.NUMBER),
            Map.entry("currency", TokenType.STRING),
            Map.entry("merchantId", TokenType.STRING),
            Map.entry("ipAddress", TokenType.STRING),
            Map.entry("deviceId", TokenType.STRING),
            Map.entry("user.id", TokenType.STRING),
            Map.entry("user.age", TokenType.NUMBER),
            Map.entry("user.region", TokenType.STRING),
            Map.entry("user.email", TokenType.STRING),
            Map.entry("user.fullName", TokenType.STRING),
            Map.entry("user.gender", TokenType.STRING),
            Map.entry("user.maritalStatus", TokenType.STRING),
            Map.entry("user.role", TokenType.STRING),
            Map.entry("user.isActive", TokenType.STRING),
            Map.entry("merchantCategoryCode", TokenType.STRING),
            Map.entry("channel", TokenType.STRING),
            Map.entry("location.country", TokenType.STRING),
            Map.entry("location.city", TokenType.STRING),
            Map.entry("location.latitude", TokenType.NUMBER),
            Map.entry("location.longitude", TokenType.NUMBER)
    );

    public ComparisonNode(String field, String operator, String expectedValue, TokenType valueType) {
        if (!FIELD_TYPES.containsKey(field)) {
            throw new InvalidFieldException();
        }

        TokenType fieldType = FIELD_TYPES.get(field);

        if (fieldType == TokenType.NUMBER && valueType == TokenType.STRING)
            if (!ALLOW_NUMBER_COMPARISON.contains(field))
                throw new InvalidOperatorException();

        if (fieldType == TokenType.STRING && valueType == TokenType.NUMBER)
            if (!ALLOW_NUMBER_COMPARISON.contains(field))
                throw new InvalidOperatorException();

        if (fieldType == TokenType.STRING)
            if (!operator.equals("=") && !operator.equals("!="))
                throw new InvalidOperatorException();


        this.field = field;
        this.operator = operator;
        this.expectedValue = expectedValue;
        this.valueType = valueType;
    }

    @Override
    public boolean evaluate(RuleEvaluationContext context) {
        Object fieldValue = context.getFieldValue(field);
        if(fieldValue == null) return false;

        boolean expectedIsString = expectedValue.startsWith("'");
        boolean expectedIsNumber = !expectedIsString;

        boolean actualIsNumber = fieldValue instanceof Number;
        boolean actualIsString = fieldValue instanceof String;

        if (actualIsNumber && expectedIsString) {
            throw new InvalidOperatorException();
        }
        if (actualIsString && expectedIsNumber) {
            throw new InvalidOperatorException();
        }

        if (actualIsNumber) {
            double actual = ((Number) fieldValue).doubleValue();
            double expected = Double.parseDouble(expectedValue);
            return compareNumbers(actual, expected);
        } else {
            String cleanExpected = expectedValue.replace("'", "");
            if (cleanExpected.endsWith(".0")) {
                cleanExpected = cleanExpected.substring(0, cleanExpected.length() - 2);
            }

            return compareStrings(fieldValue.toString(), cleanExpected);
        }
    }

    private boolean compareNumbers(double actualValue, double expectedValue) {
        Boolean result = switch (operator) {
            case ">" -> actualValue > expectedValue;
            case "<" -> actualValue < expectedValue;
            case "=" -> actualValue == expectedValue;
            case "!=" -> actualValue != expectedValue;
            case ">=" -> actualValue >= expectedValue;
            case "<=" -> actualValue <= expectedValue;
            default -> null;
        };

        return Optional.ofNullable(result).orElseThrow(InvalidOperatorException::new);
    }

    private boolean compareStrings(String actualValue, String expectedValue) {
        Boolean result = switch (operator) {
            case "=" -> actualValue.equals(expectedValue);
            case "!=" -> !actualValue.equals(expectedValue);
            default -> null;
        };

        return Optional.ofNullable(result).orElseThrow(InvalidOperatorException::new);
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String toString() {
        String valStr = valueType == TokenType.NUMBER ? expectedValue : "'" + expectedValue + "'";

        return field + " " + operator + " " + valStr;
    }
}
