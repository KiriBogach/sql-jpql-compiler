package elements;

public enum TipoJoin {
	JOIN, INNER, LEFT, RIGHT, FULLOUTER, LEFTOUTER, RIGHTOUTER, CROSS, NATURAL, FULL, CROSSAPPLY, OUTERAPPLY, STRAIGHT,
	UNION, NESTED, NATURAL_FULL, NATURAL_FULLOUTER, NATURAL_INNER, NATURAL_LEFT, NATURAL_RIGHT, NATURAL_LEFTOUTER,
	NATURAL_RIGHTOUTER, LEFTSEMI;

	public static String getJPQLJoin(TipoJoin tipo) {
		switch (tipo) {
		case FULLOUTER:
			return "FULL OUTER";
		case RIGHTOUTER:
			return "RIGHT OUTER";
		case LEFTOUTER:
			return "LEFT OUTER";
		default:
			return tipo.toString();
		}
	}
}
