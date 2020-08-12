package rentsells.security.model;

public class JwtValidation  {
	private  String token;
	
	public JwtValidation(String token, String username) {
		super();
		this.token = token;
	}
	
	@Override
	public String toString() {
		return "JwtValidation [token=" + token + "]";
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
