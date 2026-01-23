package net.korperka.antifraud.dsl.parser;

import net.korperka.antifraud.dsl.node.*;
import static net.korperka.antifraud.dsl.node.LogicalNodes.*;
import net.korperka.antifraud.dsl.token.Token;
import net.korperka.antifraud.dsl.token.TokenType;
import net.korperka.antifraud.dsl.token.Tokenizer;
import net.korperka.antifraud.exception.DslParseException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DslParser {
    private final List<Token> tokens;
    private int pos = 0;
    private final String expression;

    public DslParser(String expression) {
        this.expression = expression;
        this.tokens = new Tokenizer().tokenize(expression);
    }

    public static String normalizeExpressionSafe(String expression) {
        try {
            return new DslParser(expression).parse().toString();
        } catch (Exception e) {
            return expression;
        }
    }

    public Node parse() {
        Node root = parseExpression();

        if (pos < tokens.size() && tokens.get(pos).getType() != TokenType.EOF) {
            throw new DslParseException(pos, StringUtils.substring(expression, Math.max(pos - 2, 0), pos + 2));
        }

        return root;
    }

    private Node parseExpression() {
        Node left = parseTerm();

        while (isLogicalOp("OR")) {
            consume(TokenType.LOGICAL_OP);
            Node right = parseTerm();
            left = new OrNode(left, right);
        }

        return left;
    }

    private Node parseTerm() {
        Node left = parseFactor();

        while (isLogicalOp("AND")) {
            consume(TokenType.LOGICAL_OP);
            Node right = parseFactor();
            left = new AndNode(left, right);
        }

        return left;
    }

    private Node parseFactor() {
        if (check(TokenType.NOT_OP)) {
            consume(TokenType.NOT_OP);
            return new NotNode(parseFactor());
        }

        if (check(TokenType.L_BRACKET)) {
            consume(TokenType.L_BRACKET);
            Node node = parseExpression();
            if (!check(TokenType.R_BRACKET)) {
                throw new DslParseException(pos, StringUtils.substring(expression,  Math.max(pos - 2, 0), pos + 2));
            }
            consume(TokenType.R_BRACKET);
            return node;
        }

        return parseComparison();
    }

    private Node parseComparison() {
        Token field = consume(TokenType.FIELD);
        Token op = consume(TokenType.COMPARE_OP);

        Token value;
        if (check(TokenType.NUMBER)) {
            value = consume(TokenType.NUMBER);
        } else {
            value = consume(TokenType.STRING);
        }

        return new ComparisonNode(field.getValue(), op.getValue(), value.getValue(), value.getType());
    }

    private boolean isLogicalOp(String expectedValue) {
        if (pos >= tokens.size()) return false;
        Token t = tokens.get(pos);

        return t.getType() == TokenType.LOGICAL_OP && t.getValue().equalsIgnoreCase(expectedValue);
    }

    private boolean check(TokenType type) {
        if (pos >= tokens.size()) return false;

        return tokens.get(pos).getType() == type;
    }

    private Token consume(TokenType type) {
        if (check(type)) return tokens.get(pos++);

        Token actual = (pos < tokens.size()) ? tokens.get(pos) : new Token(TokenType.EOF, "EOF", -1);
        throw new DslParseException(pos, StringUtils.substring(expression, Math.max(pos - 2, 0), pos + 2));
    }
}