package com.rekoe.common.large.excell.to.bean.ta.exceptions;

/**
 * 
 * @author Taleh Algayev
 * Jun 1, 2018
 */
public class OpenException extends RuntimeException {

  public OpenException() {
    super();
  }

  public OpenException(String msg) {
    super(msg);
  }

  public OpenException(Exception e) {
    super(e);
  }

  public OpenException(String msg, Exception e) {
    super(msg, e);
  }
}
