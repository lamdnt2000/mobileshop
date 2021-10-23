package com.mobileshop.group8.service;

import com.mobileshop.group8.model.Member;
import com.mobileshop.group8.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidation implements Validator {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void validate(Object target, Errors errors) {
        Member user = (Member) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"userId","Not Empty");
        if (user.getFullName().length()<3 || user.getFullName().length() > 32){
            errors.rejectValue("fullName","Size.userForm.fullName");
        }
        if (userRepository.findByUserId(user.getUserId()) != null ){
            errors.rejectValue("userId", "Duplicate.userForm.userId");
        }
        if (user.getPhone().length()<3 || user.getPhone().length() > 32){
            errors.rejectValue("phone","Size.userForm.phone");
        }
        if (user.getEmail().length()<3 || user.getEmail().length() > 32){
            errors.rejectValue("email","Size.userForm.email");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"password", "Not Empty");
        if (user.getPassword().length() <4 || user.getPassword().length() > 32){
            errors.rejectValue("password", "Size.userForm.password");
        }
        if (!user.getPassword().equals(user.getPasswordComfirm())){
            errors.rejectValue("passwordConfirm","Diff.userForm.passwordConfirm");
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Member.class.equals(clazz);
    }
}
