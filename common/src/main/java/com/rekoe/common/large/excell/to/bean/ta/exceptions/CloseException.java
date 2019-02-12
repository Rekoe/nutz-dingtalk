package com.rekoe.common.large.excell.to.bean.ta.exceptions;

/**
 * 
 * @author Taleh Algayev
 * Jun 1, 2018
 */
public class CloseException extends RuntimeException {

  public CloseException() {
    super();
  }

  public CloseException(String msg) {
    super(msg);
  }

  public CloseException(Exception e) {
    super(e);
  }

  public CloseException(String msg, Exception e) {
    super(msg, e);
  }
}
