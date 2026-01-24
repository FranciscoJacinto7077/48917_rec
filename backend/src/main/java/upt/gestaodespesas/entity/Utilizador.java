package upt.gestaodespesas.entity;

public class Utilizador {
	
	private Long id;
	private String username;
	private String gmail;
	private String password;
	
	public Long getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getGmail() {
		return gmail;
	}
	
	public void setGmail(String gmail) {
		this.gmail = gmail;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}

