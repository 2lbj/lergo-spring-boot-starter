package com.lergo.framework.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {

    private String code;
    private String[] args;
    private String message;

}