package transformer.api;

import elements.From;
import elements.GroupBy;
import elements.OrderBy;
import elements.Select;
import elements.Where;
import exceptions.SQLParserException;

public interface API {
	public void parse(String sql) throws SQLParserException;
	public void reset();
	public Select getSelect();
	public From getFrom();
	public Where getWhere();
	public OrderBy getOrderBy();
	public GroupBy getGroupBy();
	
}
