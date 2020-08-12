package rentsells.security.component;
import java.util.Map;

public class TokenManager {
		private static TokenManager instance=null;
		private static 	Map<String,String> tokenManagerMap;
		
		private TokenManager() {}
		
		public  static TokenManager getInstance() {
			if ( instance == null)
				instance = new TokenManager();
			
			return instance;
		}
		
		public Map<String,String> getTokenManagerMap()
		{
			return tokenManagerMap;
		}
}