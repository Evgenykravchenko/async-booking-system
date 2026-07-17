package ru.evgeny.asyncbookingsystem.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Long resourceId) {
        super("Resource with id " + resourceId + " was not found");
    }
}
