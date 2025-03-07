package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc.rsql;

import cz.jirutka.rsql.parser.ast.*;
import lombok.Getter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;
import java.util.Map;

public class MySqlMemberRSQLVisitor extends NoArgRSQLVisitorAdapter<String> {
    private static final Map<String, String> PARAMETER_MAPPING = Map.of(
            "userId", "user_id",
            "username", "u.username",
            "displayName", "u.display_name"
    );

    private int parameterIndex = 0;
    private boolean userTableRequired = false;

    @Getter
    private final MapSqlParameterSource parameters = new MapSqlParameterSource();

    public String getJoinStatement() {
        if(userTableRequired) {
            return "INNER JOIN users u ON m.user_id = u.id";
        } else {
            return "";
        }
    }

    @Override
    public String visit(AndNode andNode) {
        List<Node> children = andNode.getChildren();

        if (children.size() == 1) {
            return children.get(0).accept(this);
        }

        return String.format("(%s)", children.stream()
                .map((child) -> child.accept(this))
                .toList()
                .stream()
                .reduce((a, b) -> String.format("%s AND %s", a, b))
                .orElse(""));
    }

    @Override
    public String visit(OrNode orNode) {
        List<Node> children = orNode.getChildren();

        if (children.size() == 1) {
            return children.get(0).accept(this);
        }

        return String.format("(%s)", children.stream()
                .map((child) -> child.accept(this))
                .toList()
                .stream()
                .reduce((a, b) -> String.format("%s OR %s", a, b))
                .orElse(""));
    }

    @Override
    public String visit(ComparisonNode comparisonNode) {
        String selector = comparisonNode.getSelector();
        String operator = comparisonNode.getOperator().getSymbol();
        List<String> arguments = comparisonNode.getArguments();
        String parameterName = generateParameterName();

        if("username".equals(selector)) {
            userTableRequired = true;
        }

        if("displayName".equals(selector)) {
            userTableRequired = true;
        }

        if (PARAMETER_MAPPING.containsKey(selector)) {
            selector = PARAMETER_MAPPING.get(selector);
        }

        return switch (operator) {
            case "==" -> {
                parameters.addValue(parameterName, arguments.get(0));
                yield String.format("%s = :%s", selector, parameterName);
            }
            case "!=" -> {
                parameters.addValue(parameterName, arguments.get(0));
                yield String.format("%s != :%s", selector, parameterName);
            }
            case "=gt=" -> {
                parameters.addValue(parameterName, arguments.get(0));
                yield String.format("%s > :%s", selector, parameterName);
            }
            case "=lt=" -> {
                parameters.addValue(parameterName, arguments.get(0));
                yield String.format("%s < :%s", selector, parameterName);
            }
            case "=ge=" -> {
                parameters.addValue(parameterName, arguments.get(0));
                yield String.format("%s >= :%s", selector, parameterName);
            }
            case "=le=" -> {
                parameters.addValue(parameterName, arguments.get(0));
                yield String.format("%s <= :%s", selector, parameterName);
            }
            case "=in=" -> {
                parameters.addValue(parameterName, arguments);
                yield String.format("%s IN (:%s)", selector, parameterName);
            }
            case "=out=" -> {
                parameters.addValue(parameterName, arguments);
                yield String.format("%s NOT IN (:%s)", selector, parameterName);
            }
            case "=like=" -> {
                parameters.addValue(parameterName, arguments.get(0));
                yield String.format("%s LIKE :%s", selector, parameterName);
            }
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operator);
        };
    }

    private String generateParameterName() {
        return String.format("rsql_param_%d", parameterIndex++);
    }
}
