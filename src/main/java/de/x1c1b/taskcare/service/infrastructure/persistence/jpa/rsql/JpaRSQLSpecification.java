package de.x1c1b.taskcare.service.infrastructure.persistence.jpa.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JpaRSQLSpecification<T> implements Specification<T> {

    private final String property;
    private final ComparisonOperator operator;
    private final List<String> arguments;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Object> arguments = castArguments(root);
        Object argument = arguments.get(0);

        switch (JpaRSQLOperator.fromValue(operator)) {
            case EQUAL -> {
                if (argument instanceof String) {
                    return criteriaBuilder.like(root.get(property), argument.toString().replace('*', '%'));
                } else if (argument == null) {
                    return criteriaBuilder.isNull(root.get(property));
                } else {
                    return criteriaBuilder.equal(root.get(property), argument);
                }
            }
            case NOT_EQUAL -> {
                if (argument instanceof String) {
                    return criteriaBuilder.notLike(root.get(property), argument.toString().replace('*', '%'));
                } else if (argument == null) {
                    return criteriaBuilder.isNotNull(root.get(property));
                } else {
                    return criteriaBuilder.notEqual(root.get(property), argument);
                }
            }
            case GREATER_THAN -> {
                return criteriaBuilder.greaterThan(root.get(property), argument.toString());
            }
            case GREATER_THAN_OR_EQUAL -> {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(property), argument.toString());
            }
            case LESS_THAN -> {
                return criteriaBuilder.lessThan(root.get(property), argument.toString());
            }
            case LESS_THAN_OR_EQUAL -> {
                return criteriaBuilder.lessThanOrEqualTo(root.get(property), argument.toString());
            }
            case IN -> {
                return root.get(property).in(arguments);
            }
            case NOT_IN -> {
                return criteriaBuilder.not(root.get(property).in(arguments));
            }
            default -> {
                return null;
            }
        }
    }

    private List<Object> castArguments(final Root<T> root) {

        final Class<?> type = root.get(property).getJavaType();

        return arguments.stream().map(argument -> {
            if (type.equals(Integer.class)) {
                return Integer.parseInt(argument);
            } else if (type.equals(Long.class)) {
                return Long.parseLong(argument);
            } else {
                return argument;
            }
        }).collect(Collectors.toList());
    }
}
