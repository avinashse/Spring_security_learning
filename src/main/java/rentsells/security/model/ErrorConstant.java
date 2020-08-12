package rentsells.security.model;

public enum ErrorConstant
{
	SUCCESS (200,"Validation success."),
	EMPTY_USERNAME (100,"Empty input username"),
	EMPTY_TOKEN (101,"Empty input token"),
	INVALID_TOKEN (102,"Invalid input token"),
	TOKEN_IS_EXPIRED (103,"Input token is expired");

	private final long errorCode;
	private final String errorMsg;

	public long getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	
	ErrorConstant(int errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
}
