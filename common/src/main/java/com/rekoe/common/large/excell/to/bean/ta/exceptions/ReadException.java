package com.rekoe.common.large.excell.to.bean.ta.exceptions;
/**
 * 
 * @author Taleh Algayev
 * Jun 1, 2018
 */
public class ReadException extends RuntimeException {

  public ReadException() {
    super();
  }

  public ReadException(String msg) {
    super(msg);
  }

  public ReadException(Exception e) {
    super(e);
  }

  public ReadException(String msg, Exception e) {
    super(msg, e);
  }
}
