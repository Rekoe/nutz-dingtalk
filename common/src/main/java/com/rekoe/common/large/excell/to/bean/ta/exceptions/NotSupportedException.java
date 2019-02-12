package com.rekoe.common.large.excell.to.bean.ta.exceptions;
/**
 * 
 * @author Taleh Algayev
 * Jun 1, 2018
 */
public class NotSupportedException extends RuntimeException {

  public NotSupportedException() {
    super();
  }

  public NotSupportedException(String msg) {
    super(msg);
  }

  public NotSupportedException(Exception e) {
    super(e);
  }

  public NotSupportedException(String msg, Exception e) {
    super(msg, e);
  }
}
