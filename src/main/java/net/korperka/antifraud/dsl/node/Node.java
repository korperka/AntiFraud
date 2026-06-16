package net.korperka.antifraud.dsl.node;

import net.korperka.antifraud.dsl.parser.RuleEvaluationContext;

public interface Node {
    boolean evaluate(RuleEvaluationContext context);
    String toString();
    int getPriority();
}
