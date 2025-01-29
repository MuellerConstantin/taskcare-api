package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import lombok.Getter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.Set;

public class MySqlUserRSQLConverter {
    private final RSQLParser parser;
    private final MySqlUserRSQLVisitor visitor;

    public MySqlUserRSQLConverter() {
        Set<ComparisonOperator> operators = RSQLOperators.defaultOperators();
        operators.add(new ComparisonOperator("=like="));

        parser = new RSQLParser(operators);
        visitor = new MySqlUserRSQLVisitor();
    }

    @Getter
    private String query;

    @Getter
    private MapSqlParameterSource parameters;

    public void parse(String predicate) {
        Node rootNode = parser.parse(predicate);
        query = rootNode.accept(visitor);
        parameters = visitor.getParameters();
    }
}
