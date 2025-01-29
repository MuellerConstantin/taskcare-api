package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.rsql;

import cz.jirutka.rsql.parser.ast.*;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.InvalidSearchParameterException;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.Searchable;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public class SearchFilterValidationRSQLVisitor extends NoArgRSQLVisitorAdapter<Node> {
    private final Class<?> type;

    @Override
    public Node visit(AndNode andNode) {
        return andNode;
    }

    @Override
    public Node visit(OrNode orNode) {
        return orNode;
    }

    @Override
    public Node visit(ComparisonNode comparisonNode) {
        String selector = comparisonNode.getSelector();

        boolean isFieldPresent = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Searchable.class))
                .anyMatch(field -> field.getName().equals(selector));

        if (!isFieldPresent) {
            throw new InvalidSearchParameterException(selector);
        }

        return comparisonNode;
    }
}
