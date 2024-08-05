package de.x1c1b.taskcare.api.infrastructure.persistence.jpa.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.jpa.domain.Specification;

public class JpaRSQLVisitor<T> implements RSQLVisitor<Specification<T>, Void> {

    private final JpaRSQLSpecificationBuilder<T> jpaRSQLSpecificationBuilder;

    public JpaRSQLVisitor() {
        this.jpaRSQLSpecificationBuilder = new JpaRSQLSpecificationBuilder<>();
    }

    @Override
    public Specification<T> visit(AndNode andNode, Void unused) {
        return jpaRSQLSpecificationBuilder.createSpecification(andNode);
    }

    @Override
    public Specification<T> visit(OrNode orNode, Void unused) {
        return jpaRSQLSpecificationBuilder.createSpecification(orNode);
    }

    @Override
    public Specification<T> visit(ComparisonNode comparisonNode, Void unused) {
        return jpaRSQLSpecificationBuilder.createSpecification(comparisonNode);
    }
}
