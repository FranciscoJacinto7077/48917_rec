package upt.gestaodespesas.controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
	
	public List<String> listarCategorias() throws SQLException {
		String sql = "SELECT id, nome FROM categorias order by id";
		List<String> out = new ArrayList<>();
		
		try (Connection c = getConn();
			PreparedStatement ps = c.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {
			
				while (rs.next()) {
					out.add(rs.getLong("id") + " - " + rs.getString("nome"));
				}
		}
		return out;
	}
	
	public boolean categoriaExiste(long id) throws SQLException {
		String sql = "SELECT COUNT(*) FROM categorias WHERE id = ?";
		try (Connection c = getConn();
			PreparedStatement ps = c.prepareStatement(sql)) {
			
				ps.setLong(1, id);
				try (ResultSet rs = ps.executeQuery()) {
						return rs.next();
				}

			}
	}

	public long inserirDespesa(String descricao, double valor, Date data, String metodoPagamento, long categoriaId) throws SQLException {
		String sql = "INSERT INTO despesas (descricao, valor, data, metodo_pagamento, categoria_id) VALUES (?, ?, ?, ?)";
		try (Connection c = getConn();
			PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
				ps.setString(1, descricao);
				ps.setDouble(2, valor);
				ps.setDate(3, data);
				ps.setString(4, metodoPagamento);
				ps.setLong(5, categoriaId);
				ps.executeUpdate();
				
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						return rs.getLong(1);
			}
			return -1;
		}
	}
	
	public List<String> listarDespesas() throws SQLException {
		String sql = "SELECT d.id, d.descricao, d.valor, d.data, d.metodo_pagamento, c.nome AS categoria_nome " +
					 "FROM despesas d " +
					 "JOIN categorias c ON d.categoria_id = c.id " +
					 "ORDER BY d.id";
		List<String> out = new ArrayList<>();
		
		try (Connection c = getConn();
			PreparedStatement ps = c.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {
			
				while (rs.next()) {
					out.add(rs.getLong("id") + " - " +
							rs.getString("descricao") + " - " +
							rs.getDouble("valor") + " - " +
							rs.getDate("data") + " - " +
							rs.getString("metodo_pagamento") + " - " +
							rs.getString("categoria_nome"));
				}
		}
		return out;
	}
}
