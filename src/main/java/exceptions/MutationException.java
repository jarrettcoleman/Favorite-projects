package exceptions;

public class MutationException extends RuntimeException {

	protected String msg;

	public MutationException(String msg) {
		this.msg = msg;
	}

	@Override
	public String getMessage() {
		return "Mutation error: " + msg;
	}
}
