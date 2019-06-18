package transformer.factory;

import transformer.service.JPQLTransformer;
import transformer.service.gsp.GSPTransformer;

public class JPQLTransformerFactory {

	public static JPQLTransformer getInstance(JPQLTransformers type) {
		switch (type) {
		case GSP:
			return new GSPTransformer();
		default:
			return null;
		}
	}
}
