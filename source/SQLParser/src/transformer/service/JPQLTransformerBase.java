package transformer.service;

import elements.From;
import elements.GroupBy;
import elements.OrderBy;
import elements.Select;
import elements.Where;
import transformer.api.API;

public abstract class JPQLTransformerBase implements JPQLTransformer {

	protected API api;
	protected Select select;
	protected From from;
	protected Where where;
	protected OrderBy orderBy;
	protected GroupBy groupBy;
	
	public JPQLTransformerBase() {
		this.select = new Select();
		this.from = new From();
		this.where = new Where();
		this.orderBy = new OrderBy();
		this.groupBy = new GroupBy();
	}
	
	@Override
	public abstract String transform(String sql);

}
