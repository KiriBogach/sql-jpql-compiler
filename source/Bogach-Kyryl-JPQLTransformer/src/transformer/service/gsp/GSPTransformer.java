package transformer.service.gsp;

import transformer.api.gsp.GSPParserAPI;
import transformer.service.JPQLTransformer;

// (?) Sutituir javafx.util.Pair por 
// https://stackoverflow.com/questions/521171/a-java-collection-of-value-pairs-tuples
// AbstractMap.SimpleEntry

public class GSPTransformer extends JPQLTransformer {

	public GSPTransformer() {
		super();
		this.api = new GSPParserAPI();
	}
}
