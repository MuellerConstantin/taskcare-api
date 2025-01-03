package de.mueller_constantin.taskcare.api.core.common.application.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Centralized domain validation aspect. Responsible for validating domain input.
 *
 * <p>
 *     This aspect is applied to all methods that have at least one parameter
 *     annotated with {@link Valid} and are part of a domain class annotated with
 *     {@link Validated}.
 * </p>
 */
@Aspect
@RequiredArgsConstructor
public class DomainValidationAspect {
    private final Validator validator;

    @Before("within(de.mueller_constantin.taskcare.api.core..*) && @within(de.mueller_constantin.taskcare.api.core.common.application.validation.Validated) && execution(* *(.., @jakarta.validation.Valid (*), ..))")
    public void validate(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        for (int index = 0; index < parameterAnnotations.length; index++) {
            for (Annotation annotation : parameterAnnotations[index]) {
                if (annotation.annotationType().equals(Valid.class)) {
                    Set<ConstraintViolation<Object>> violations = validator.validate(args[index]);

                    if(!violations.isEmpty()) {
                        throw new ConstraintViolationException(violations);
                    }
                }
            }
        }
    }
}
