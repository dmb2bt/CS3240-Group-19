package Commands;

public interface iCommand {
	public boolean parseArguments(String ... args);
	public boolean performAction();
}
