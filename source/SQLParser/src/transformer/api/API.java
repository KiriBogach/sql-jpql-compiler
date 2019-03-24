package transformer.api;

import elements.From;
import elements.Select;
import elements.Where;

public interface API {
	public void parse(String sql);
	public Select getSelect();
	public From getFrom();
	public Where getWhere();
	
}
