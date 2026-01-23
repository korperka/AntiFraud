package net.korperka.antifraud.dsl.token;

import java.util.List;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Tokenizer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "\\s+|" +
                    "'([^']*)'|" +
                    "(\\d+(?:\\.\\d+)?)|" +
                    "(>=|<=|!=|[><=])|" +
                    "([()])|" +
                    "([a-zA-Z_][a-zA-Z0-9_.]*)"
    );

    public List<Token> tokenize(String expression) {
        List<Token> tokens = TOKEN_PATTERN.matcher(expression)
                .results()
                .map(this::mapToToken)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        tokens.add(new Token(TokenType.EOF, "", expression.length()));
        return tokens;
    }

    private Token mapToToken(MatchResult match) {
        int pos = match.start();

        if (match.group(1) != null) return new Token(TokenType.STRING, match.group(1), pos);
        if (match.group(2) != null) return new Token(TokenType.NUMBER, match.group(2), pos);
        if (match.group(3) != null) return new Token(TokenType.COMPARE_OP, match.group(3), pos);
        if (match.group(4) != null) return new Token(match.group(4).equals("(") ? TokenType.L_BRACKET : TokenType.R_BRACKET, match.group(4), pos);

        String word = match.group(5);
        if (word != null) {
            if (List.of("AND", "OR").contains(word.toUpperCase())) return new Token(TokenType.LOGICAL_OP, word.toUpperCase(), pos);
            if ("NOT".equalsIgnoreCase(word)) return new Token(TokenType.NOT_OP, word.toUpperCase(), pos);
            return new Token(TokenType.FIELD, word, pos);
        }

        return null;
    }
}