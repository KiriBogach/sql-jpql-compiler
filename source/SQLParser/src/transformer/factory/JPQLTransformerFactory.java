package transformer.factory;

import transformer.service.GSP_JPQLTransformer;
import transformer.service.JPQLTransformer;

public class JPQLTransformerFactory {

	public static JPQLTransformer getInstance(JPQLTransformers type) {
		switch (type) {
		case GSP:
			return new GSP_JPQLTransformer();
		default:
			return null;
		}
	}
}
