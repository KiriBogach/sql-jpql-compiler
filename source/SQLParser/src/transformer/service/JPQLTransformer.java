package transformer.service;

import exceptions.SQLParserException;

public interface JPQLTransformer {
	public String transform(String sql) throws SQLParserException;
}
