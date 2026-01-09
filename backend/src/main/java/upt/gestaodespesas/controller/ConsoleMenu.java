package upt.gestaodespesas.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
	
	public static void main(String[] args) {
		
		String url = "jdbc:postgresql://localhost:5432/gestaodespesas";
		String user = "root";
		String pass = "pass";
		
		DBService db = new DBService(url, user, pass);
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			
			System.out.println("Menu de Categorias");
			System.out.println("1 - Inserir Categoria");
			System.out.println("2 - Listar Categorias");
			System.out.println("3 - Criar Despesa (com categoria)");
			System.out.println("4 - Listar Despesas");
			System.out.println("0 - Sair");
			System.out.print("Escolha uma opcao: ");
			
			int opcao;
			try {
				opcao = Integer.parseInt(sc.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println("Entrada invalida. Tente novamente.");
				continue;
			}
			sc.nextLine(); 
			
			switch (opcao) {
				case 1:
					System.out.print("Nome da Categoria: ");
					String nome = sc.nextLine();
					try {
						long id = db.inserirCategoria(nome);
						System.out.println("Categoria inserida com ID: " + id);
					} catch (SQLException e) {
						System.err.println("Erro ao inserir categoria: " + e.getMessage());
					}
					break;
					
				case 2:
				    try {
				        List<String> categorias = db.listarCategorias();
				        System.out.println("Categorias:");

				        if (categorias.isEmpty()) {
				            System.out.println("(vazio)");
				        } else {
				            categorias.forEach(System.out::println);
				        }

				    } catch (SQLException e) {
				        System.err.println("Erro ao listar categorias: " + e.getMessage());
				    }
				    break;
				
				case 3:
					try {
						List<String> categorias = db.listarCategorias();
						System.out.println("Categorias Disponiveis:");
						if (categorias.isEmpty()) {
							System.out.println("Não há categorias disponíveis. Cria uma categoria primeiro.");
							break;
						}
						categorias.forEach(System.out::println);
						
						System.out.print("ID da Categoria: ");
						long categoriaId = Long.parseLong(sc.nextLine().trim());
						
						System.out.print("Descricao da Despesa: ");
						String descricao = sc.nextLine().trim();
						
						System.out.print("Valor da Despesa: ");
						double valor = Double.parseDouble(sc.nextLine().trim());
						
						System.out.print("Data da Despesa (YYYY-MM-DD): ");
						String dataStr = sc.nextLine().trim();
						
						System.out.print("Metodo de Pagamento: ");
						String metodoPagamento = sc.nextLine().trim();
						
						long despesaId = db.inserirDespesa(descricao, valor, java.sql.Date.valueOf(dataStr), metodoPagamento, categoriaId);
						System.out.println("Despesa inserida com ID: " + despesaId);
						
					} catch (SQLException e) {
						System.err.println("Erro ao inserir despesa: " + e.getMessage());
					} catch (NumberFormatException e) {
						System.err.println("Data invalida. Use o formato YYYY-MM-DD.");
					} catch (IllegalArgumentException e) {
						System.err.println("Entrada numerica invalida. Tente novamente.");
					}
					break;
					
				case 0:
					System.out.println("Saindo...");
					sc.close();
					return;
				default:
					System.out.println("Opcao invalida. Tente novamente.");
			}
		}
	}
}
