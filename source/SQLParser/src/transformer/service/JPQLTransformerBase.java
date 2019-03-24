package transformer.service;

import elements.From;
import elements.Select;
import elements.Where;
import transformer.api.API;

public abstract class JPQLTransformerBase implements JPQLTransformer {

	protected API api;
	protected Select select;
	protected From from;
	protected Where where;
	
	public JPQLTransformerBase() {
		this.select = new Select();
		this.from = new From();
		this.where = new Where();
	}
	
	@Override
	public abstract String transform(String sql);

}
