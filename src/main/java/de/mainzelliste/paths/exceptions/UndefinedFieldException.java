package de.mainzelliste.paths.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

public class UndefinedFieldException extends WebApplicationException {

  /**
   * Construct a new instance with default HTTP status code 400.
   *
   * @param fieldName undefined field name.
   */
  public UndefinedFieldException(String fieldName) {
    super(String.format("Undefined input field '%s'.", fieldName), Status.BAD_REQUEST);
  }
}
