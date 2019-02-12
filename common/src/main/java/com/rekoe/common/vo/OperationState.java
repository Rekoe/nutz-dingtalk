package com.rekoe.common.vo;

public enum OperationState {

	/**
	 * 成功
	 */
	SUCCESS("成功", 0),
	/**
	 * 失败
	 */
	FAIL("失败", 1),
	/**
	 * 异常
	 */
	EXCEPTION("异常发生", -1);

	String mgs;

	int code;

	/**
	 * @param mgs
	 * @param code
	 */
	private OperationState(String mgs, int code) {
		this.mgs = mgs;
		this.code = code;
	}

	/**
	 * @return the mgs
	 */
	public String getMgs() {
		return mgs;
	}

	/**
	 * @param mgs
	 *            the mgs to set
	 */
	public void setMgs(String mgs) {
		this.mgs = mgs;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}


}
