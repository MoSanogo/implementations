package com.implementations.implementations.exceptions;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ResourceNotFoundException extends RuntimeException{
    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public ResourceNotFoundException(String message, String resourceName,
                                     String fieldName, Object fieldValue) {
        super(String.format(message, resourceName,fieldName,fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
