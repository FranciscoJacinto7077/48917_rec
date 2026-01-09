package upt.gestaodespesas.controller;

import java.sql.*;

public class DBService {
	
	private final String url;
	private final String user;
	private final String pass;
	
	public DBService(String url, String user, String pass) {
		this.url = url;
		this.user = user;
		this.pass = pass;
	}
	
	private Connection getConn() throws SQLException {
		return DriverManager.getConnection(url, user, pass);
	}
	
	public long inserirCategoria(String nome) throws SQLException {
		String sql = "INSERT INTO categorias (nome) VALUES (?)";
		try (Connection c = getConn();
			PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
				ps.setString(1, nome);
				ps.executeUpdate();
				
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						return rs.getLong(1);
					}
					return -1;
				}
		}
	}
}
