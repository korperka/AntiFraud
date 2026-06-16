package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data @AllArgsConstructor
public class RuleMatchRow {
    private UUID ruleId;
    private String ruleName;
    private long matches;
    private long uniqueUsers;
    private long uniqueMerchants;
    private double shareOfDeclines;
}
