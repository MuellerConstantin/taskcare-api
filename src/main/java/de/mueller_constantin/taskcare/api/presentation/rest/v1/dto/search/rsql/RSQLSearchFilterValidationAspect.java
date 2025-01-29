package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.SearchFilter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

/**
 * Aspect for validating search filters. The search filter is expected to be a RSQL string.
 *
 * <p>
 *     This aspect is applied to all methods of {@link org.springframework.web.bind.annotation.RestController @RestController}
 *     classes that have a parameter annotated with {@link SearchFilter @SearchFilter}. It will ensure that all
 *     selectors in the search filter are searchable fields, means fields that are annotated with
 *     {@link de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.Searchable @Searchable}, of the return
 *     type of the method.
 * </p>
 *
 * @see de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.Searchable Searchable
 * @see de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.SearchFilter SearchFilter
 * @see de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.InvalidSearchParameterException InvalidSearchParameterException
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RSQLSearchFilterValidationAspect {
    @Before("within(de.mueller_constantin.taskcare.api.presentation.rest.v1.*) && @within(org.springframework.web.bind.annotation.RestController) && execution(* *(.., @de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.SearchFilter (*), ..))")
    public void validate(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();
        Class<?> returnType = extractReturnType(method);

        boolean foundSearchFilter = false;
        String searchFilter = null;

        for (int index = 0; index < parameterAnnotations.length; index++) {
            for (Annotation annotation : parameterAnnotations[index]) {
                if (annotation.annotationType().equals(SearchFilter.class)) {
                    if (foundSearchFilter) {
                        throw new IllegalArgumentException("Only one field with @SearchFilter is allowed in " + returnType.getSimpleName() + "." + method.getName() + "()");
                    }

                    foundSearchFilter = true;
                    searchFilter = (String) args[index];
                }
            }
        }

        if (searchFilter != null) {
            validate(returnType, searchFilter);
        }
    }

    private Class<?> extractReturnType(Method method) {
        Class<?> returnType = method.getReturnType();

        if (returnType == ResponseEntity.class) {
            returnType = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        }

        if(returnType == PageDto.class) {
            returnType = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        }

        return returnType;
    }

    private void validate(Class<?> returnType, String searchFilter) {
        Set<ComparisonOperator> operators = RSQLOperators.defaultOperators();
        operators.add(new ComparisonOperator("=like="));

        RSQLParser parser = new RSQLParser(operators);
        SearchFilterValidationRSQLVisitor visitor = new SearchFilterValidationRSQLVisitor(returnType);

        Node rootNode = parser.parse(searchFilter);
        rootNode.accept(visitor);
    }
}
