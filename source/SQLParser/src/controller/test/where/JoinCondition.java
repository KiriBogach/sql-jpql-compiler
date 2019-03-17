package controller.test.where;

import gudusoft.gsqlparser.TCustomSqlStatement;
import java.util.ArrayList;
import java.util.List;

enum ClauseType {
	where, connectby, startwith, orderby
}

class JoinCondition {

	public String lefttable, righttable, leftcolumn, rightcolumn;
	public List<TCustomSqlStatement> sql = new ArrayList<TCustomSqlStatement>();

	public int hashCode() {
		int hashCode = 0;
		if (lefttable != null)
			hashCode += lefttable.hashCode();
		if (righttable != null)
			hashCode += righttable.hashCode();
		if (leftcolumn != null)
			hashCode += leftcolumn.hashCode();
		if (rightcolumn != null)
			hashCode += rightcolumn.hashCode();

		for (TCustomSqlStatement stmt : sql) {
			hashCode += stmt.hashCode();
		}

		return hashCode;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof JoinCondition))
			return false;

		JoinCondition join = (JoinCondition) obj;

		if (this.leftcolumn != null && !this.leftcolumn.equals(join.leftcolumn))
			return false;
		if (this.rightcolumn != null && !this.rightcolumn.equals(join.rightcolumn))
			return false;
		if (this.lefttable != null && !this.lefttable.equals(join.lefttable))
			return false;
		if (this.righttable != null && !this.righttable.equals(join.righttable))
			return false;

		if (join.righttable != null && !join.righttable.equals(this.righttable))
			return false;
		if (join.lefttable != null && !join.lefttable.equals(this.lefttable))
			return false;
		if (join.rightcolumn != null && !join.rightcolumn.equals(this.rightcolumn))
			return false;
		if (join.leftcolumn != null && !join.leftcolumn.equals(this.leftcolumn))
			return false;

		if (join.sql.size() != this.sql.size())
			return false;
		for (int i = 0; i < join.sql.size(); i++) {
			if (!join.sql.get(i).equals(this.sql.get(i)))
				return false;
		}

		return true;
	}
}







