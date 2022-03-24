package cloneproject.Instagram.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomValidator implements Validator {

    private final SpringValidatorAdapter validator;

    @Override
    public boolean supports(Class<?> clazz) {
        return List.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        for (Object o : (Collection) target) {
            validator.validate(o, errors);
        }
    }
}
