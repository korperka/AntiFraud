package net.korperka.antifraud.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.response.MerchantRiskStats;
import net.korperka.antifraud.dto.response.RuleMatchStats;
import net.korperka.antifraud.dto.response.StatsOverviewResponse;
import net.korperka.antifraud.dto.response.TransactionsTimeSeries;
import net.korperka.antifraud.enums.TransactionChannel;
import net.korperka.antifraud.service.StatService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/stats")
@Tag(name = "Statistics", description = "Статистика/аналитика (метрики и разрезы)")
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @GetMapping("/merchants/risk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MerchantRiskStats> getMerchantRiskStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String merchantCategoryCode,
            @RequestParam(defaultValue = "50") int top
    ) {
        return ResponseEntity.ok(statService.getMerchantRiskStats(from, to, merchantCategoryCode, top));
    }

    @GetMapping("/rules/matches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RuleMatchStats> getMatchStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "20") int top
    ) {
        return ResponseEntity.ok(statService.getRuleStats(from, to, top));
    }

    @GetMapping("/transactions/timeseries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionsTimeSeries> getTimeSeries(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "day") String groupBy,
            @RequestParam(defaultValue = "UTC") String timezone,
            @RequestParam(required = false) TransactionChannel channel
    ) {
        String channelStr = channel != null ? channel.name() : null;

        return ResponseEntity.ok(statService.getTimeSeries(from, to, groupBy, timezone, channelStr));
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatsOverviewResponse> getStatsOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(statService.getOverview(from, to));
    }
}
