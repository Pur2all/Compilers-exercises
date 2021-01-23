package visitor;

public interface Visitable
{
	Object accept(Visitor visitor) throws Exception;
}
