package br.com.socialconnect.api.produtos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EstoqueNaoNegativoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EstoqueNaoNegativo {

    String message() default "{produto.estoque.nao_negativo}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
