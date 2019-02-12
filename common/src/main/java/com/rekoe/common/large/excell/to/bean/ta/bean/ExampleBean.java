package com.rekoe.common.large.excell.to.bean.ta.bean;

import com.rekoe.common.large.excell.to.bean.ta.utilities.annotations.ExcelBean;
import com.rekoe.common.large.excell.to.bean.ta.utilities.annotations.ExcelColumnIndex;
import com.rekoe.common.large.excell.to.bean.ta.utilities.bean.ResponseBean;
/**
 * 
 * @author Taleh Algayev
 * Jun 1, 2018
 */
@ExcelBean
public class ExampleBean extends ResponseBean{
	@ExcelColumnIndex(columnIndex = "0", dataType = "string")
	private String column1;
	@ExcelColumnIndex(columnIndex = "1", dataType = "string")
	private String column2;
	@ExcelColumnIndex(columnIndex = "2", dataType = "string")
	private String column3;
	@ExcelColumnIndex(columnIndex = "3", dataType = "string")
	private String column4;
	@ExcelColumnIndex(columnIndex = "4", dataType = "string")
	private String column5;
	@ExcelColumnIndex(columnIndex = "5", dataType = "string")
	private String column6;
	@ExcelColumnIndex(columnIndex = "6", dataType = "string")
	private String column7;
	@ExcelColumnIndex(columnIndex = "7", dataType = "string")
	private String column8;
	@ExcelColumnIndex(columnIndex = "8", dataType = "string")
	private String column9;
	@ExcelColumnIndex(columnIndex = "9", dataType = "string")
	private String column10;
	public String getColumn1() {
		return column1;
	}
	public void setColumn1(String column1) {
		this.column1 = column1;
	}
	public String getColumn2() {
		return column2;
	}
	public void setColumn2(String column2) {
		this.column2 = column2;
	}
	public String getColumn3() {
		return column3;
	}
	public void setColumn3(String column3) {
		this.column3 = column3;
	}
	public String getColumn4() {
		return column4;
	}
	public void setColumn4(String column4) {
		this.column4 = column4;
	}
	public String getColumn5() {
		return column5;
	}
	public void setColumn5(String column5) {
		this.column5 = column5;
	}
	public String getColumn6() {
		return column6;
	}
	public void setColumn6(String column6) {
		this.column6 = column6;
	}
	public String getColumn7() {
		return column7;
	}
	public void setColumn7(String column7) {
		this.column7 = column7;
	}
	public String getColumn8() {
		return column8;
	}
	public void setColumn8(String column8) {
		this.column8 = column8;
	}
	public String getColumn9() {
		return column9;
	}
	public void setColumn9(String column9) {
		this.column9 = column9;
	}
	public String getColumn10() {
		return column10;
	}
	public void setColumn10(String column10) {
		this.column10 = column10;
	}
	public ExampleBean(String column1, String column2, String column3, String column4, String column5, String column6,
			String column7, String column8, String column9, String column10) {
		super();
		this.column1 = column1;
		this.column2 = column2;
		this.column3 = column3;
		this.column4 = column4;
		this.column5 = column5;
		this.column6 = column6;
		this.column7 = column7;
		this.column8 = column8;
		this.column9 = column9;
		this.column10 = column10;
	}
	public ExampleBean() {
		super();
	}
}
