package controller.test.where;

import java.util.ArrayList;
import java.util.List;

class TColumn {

	public List<String> tableNames = new ArrayList<String>();
	public String columnName;
	public String columnPrex;
	public String columnAlias;

	public String getFullName(String tableName) {
		if (tableName != null) {
			return tableName + "." + columnName;
		} else {
			return columnName;
		}
	}

	public String getOrigName() {
		if (columnPrex != null) {
			return columnPrex + "." + columnName;
		} else {
			return columnName;
		}
	}

}