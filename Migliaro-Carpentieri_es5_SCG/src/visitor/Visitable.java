package visitor;

public interface Visitable
{
	Boolean accept(Visitor visitor) throws Exception;
}
