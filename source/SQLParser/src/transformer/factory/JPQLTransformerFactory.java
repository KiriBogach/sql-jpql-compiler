package transformer.factory;

import transformer.service.JPQLTransformer;
import transformer.service.gsp.GSP_JPQLTransformer;

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
