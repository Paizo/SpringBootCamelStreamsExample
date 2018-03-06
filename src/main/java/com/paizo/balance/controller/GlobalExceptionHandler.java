package com.paizo.balance.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String catchAllException(Exception e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("message", (e.getMessage() == null ? "An Internal error has occurred" : e.getMessage()));
        return "redirect:/errorPage";

    }

}