package visitor;

import utils.Pair;

public interface Visitable
{
	 Pair<Boolean, String> accept(Visitor visitor) throws Exception;
}
