package com.rekoe.common.large.excell.to.bean.ta.utilities.bean;

import com.rekoe.common.large.excell.to.bean.ta.utilities.enums.CoreMessages;
/**
 * 
 * @author Taleh Algayev
 * Jun 1, 2018
 */
public class ResponseBean {

	private long rowId;

	private CoreMessages errorCode;

	public CoreMessages getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(CoreMessages errorCode) {
		this.errorCode = errorCode;
	}

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}


}
