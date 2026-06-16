package net.korperka.antifraud.dsl.node;

import lombok.AllArgsConstructor;
import net.korperka.antifraud.dsl.parser.RuleEvaluationContext;

public class LogicalNodes {
    @AllArgsConstructor
    public static class AndNode implements Node {
        private final Node left;
        private final Node right;

        @Override
        public boolean evaluate(RuleEvaluationContext context) {
            return left.evaluate(context) && right.evaluate(context);
        }

        @Override
        public int getPriority() {
            return 2;
        }

        @Override
        public String toString() {
            String leftStr = left.toString();
            if (left.getPriority() < getPriority()) {
                leftStr = "(" + leftStr + ")";
            }

            String rightStr = right.toString();
            if (right.getPriority() < getPriority()) {
                rightStr = "(" + rightStr + ")";
            }

            return leftStr + " AND " + rightStr;
        }
    }

    @AllArgsConstructor
    public static class OrNode implements Node {
        private final Node left;
        private final Node right;

        @Override
        public boolean evaluate(RuleEvaluationContext context) {
            return left.evaluate(context) || right.evaluate(context);
        }

        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public String toString() {
            String leftStr = left.toString();
            if (left.getPriority() < getPriority()) {
                leftStr = "(" + leftStr + ")";
            }

            String rightStr = right.toString();
            if (right.getPriority() < getPriority()) {
                rightStr = "(" + rightStr + ")";
            }

            return leftStr + " OR " + rightStr;
        }
    }

    @AllArgsConstructor
    public static class NotNode implements Node {
        private final Node child;

        @Override
        public boolean evaluate(RuleEvaluationContext context) {
            return !child.evaluate(context);
        }

        @Override
        public int getPriority() {
            return 3;
        }

        @Override
        public String toString() {
            String childStr = child.toString();
            if (child.getPriority() < getPriority()) {
                childStr = "(" + childStr + ")";
            }
            return "NOT " + childStr;
        }
    }
}
