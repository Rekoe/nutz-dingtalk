package com.rekoe.common.large.excell.to.bean.ta.utilities.enums;

import com.rekoe.common.large.excell.to.bean.ta.utilities.annotations.ExcelBean;
import com.rekoe.common.large.excell.to.bean.ta.utilities.annotations.ExcelColumnIndex;

/**
 * Defines the excel factory type.<br>
 * Based on this ExcelFactory, you can choose whether you want to extract cell
 * values based on (columns names) or (column index).
 * 
 * @author Taleh Alqayev
 * @see ExcelBean
 * @see ExcelColumnIndex
 */
public enum ExcelFactoryType {
    COLUMN_INDEX_BASED_EXTRACTION, COLUMN_NAME_BASED_EXTRACTION;
}
