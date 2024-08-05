package de.x1c1b.taskcare.api.infrastructure.persistence.jpa.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum JpaRSQLOperator {

    EQUAL(RSQLOperators.EQUAL),
    NOT_EQUAL(RSQLOperators.NOT_EQUAL),
    GREATER_THAN(RSQLOperators.GREATER_THAN),
    GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),
    LESS_THAN(RSQLOperators.LESS_THAN),
    LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
    IN(RSQLOperators.IN),
    NOT_IN(RSQLOperators.NOT_IN),
    LIKE(new ComparisonOperator("=like=", false));

    private final ComparisonOperator value;

    public static JpaRSQLOperator fromValue(ComparisonOperator value) {
        return Arrays.stream(values()).filter(operator -> operator.getValue() == value)
                .findAny().orElseThrow(IllegalArgumentException::new);
    }
}
