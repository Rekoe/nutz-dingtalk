package com.rekoe.common.large.excell.to.bean.ta.exceptions;
/**
 * 
 * @author Taleh Algayev
 * Jun 1, 2018
 */
public class MissingSheetException extends RuntimeException {

  public MissingSheetException() {
    super();
  }

  public MissingSheetException(String msg) {
    super(msg);
  }

  public MissingSheetException(Exception e) {
    super(e);
  }

  public MissingSheetException(String msg, Exception e) {
    super(msg, e);
  }
}
