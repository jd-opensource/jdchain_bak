package com.jd.blockchain.utils.http.agent;


/***
 * Http Authorization Header；
 * 
 * @author haiq
 *
 */
public class AuthorizationHeader implements RequestHeader{

	/**@see com.jd.blockchain.utils.http.agent.AuthorizationAlgs*/
	private String alg;				//算法类型
	
	private String senderName;		//开发者注册的用户名
	
	private String secretKey;		//秘钥
	
	public AuthorizationHeader(){
		
	}
	
	public AuthorizationHeader(String senderName, String secretKey){
		this(AuthorizationAlgs.DEFAULT, senderName, secretKey);
	}
	
	public AuthorizationHeader(String alg, String senderName, String secretKey){
		this.alg = alg;
		this.senderName = senderName;
		this.secretKey = secretKey;
	}

	public String getAlg() {
		return alg;
	}

	public String getSenderName() {
		return senderName;
	}

	public String getSecretKey() {
		return secretKey;
	}

	@Override
	public String getName() {
		return "Authorization";
	}

	@Override
	public String getValue() {
		StringBuilder authBuilder = new StringBuilder();
		authBuilder.append(alg).append(" ").append(senderName).append(":")
				.append(secretKey);
		return authBuilder.toString();
	}
	
	
}
