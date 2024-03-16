package com.cdd.sangchupassport.support;

import com.cdd.sangchupassport.Passport;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;


@RequiredArgsConstructor
@Slf4j
public class PassportArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String PASSPORT_HEADER = "Sangchu-Passport";
    private final ObjectMapper mapper;
    private final PassportValidator passportValidator;


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("Passport Argument Resolver Start");
        boolean hasParameterAnnotation = parameter.hasParameterAnnotation(ParsePassport.class);
        boolean assignableFrom = Passport.class.isAssignableFrom(parameter.getParameterType());
        return hasParameterAnnotation && assignableFrom;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        Passport passport = mapper.readValue(
                Objects.requireNonNull(
                        webRequest.getNativeRequest(
                                HttpServletRequest.class)
                ).getHeader(PASSPORT_HEADER),
                Passport.class);
        passportValidator.validatePassword(passport);
        return passport;
    }
}
