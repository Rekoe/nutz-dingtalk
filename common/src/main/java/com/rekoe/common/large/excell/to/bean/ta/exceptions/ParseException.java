package com.rekoe.common.large.excell.to.bean.ta.exceptions;
/**
 * 
 * @author Taleh Algayev
 * Jun 1, 2018
 */
public class ParseException extends RuntimeException {

  public ParseException() {
    super();
  }

  public ParseException(String msg) {
    super(msg);
  }

  public ParseException(Exception e) {
    super(e);
  }

  public ParseException(String msg, Exception e) {
    super(msg, e);
  }
}
