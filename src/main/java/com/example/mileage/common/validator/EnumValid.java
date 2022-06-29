package com.example.mileage.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enum Validate를 위한 Annotation Class
 */
//  해당 Annotation이 실행 할 ConstraintValidator 구현체를 `EnumValidator`로 지정합니다.
@Constraint(validatedBy = {EnumValidator.class})
// 해당 Annotation은 필드, 파라미터에 적용 할 수 있습니다.
@Target({ElementType.FIELD,ElementType.PARAMETER})
// annotation을 Runtime까지 유지합니다.
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValid {
    String message() default "This is Invalid Value";
    Class<?>[] groups() default { };
    Class<? extends java.lang.Enum<?>> enumClass();
    Class<? extends Payload>[] payload() default { };
}
