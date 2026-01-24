package upt.gestaodespesas;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.MetodoPagamento;
import upt.gestaodespesas.repository.CategoriaRepository;
import upt.gestaodespesas.repository.DespesaRepository;

@Component
public class ConsoleMenu implements CommandLineRunner {

    private final CategoriaRepository categoriaRepo;
    private final DespesaRepository despesaRepo;

    public ConsoleMenu(CategoriaRepository categoriaRepo, DespesaRepository despesaRepo) {
        this.categoriaRepo = categoriaRepo;
        this.despesaRepo = despesaRepo;
    }

    @Override
    public void run(String... args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu");
            System.out.println("1 - Inserir Categoria");
            System.out.println("2 - Listar Categorias");
            System.out.println("3 - Criar Despesa (com categoria)");
            System.out.println("4 - Listar Despesas");
            System.out.println("5 - Eliminar Despesa");
            System.out.println("6 - Eliminar Categoria");
            System.out.println("7 - Listar Despesas por Categoria");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            int opcao = Integer.parseInt(sc.nextLine().trim());

            switch (opcao) {

                case 1:
                    System.out.print("Nome da Categoria: ");
                    String nome = sc.nextLine();
                    Categoria c = new Categoria();
                    c.setNome(nome);
                    categoriaRepo.save(c);
                    System.out.println("OK! Categoria criada.");
                    break;

                case 2:
                    System.out.println("Categorias:");
                    categoriaRepo.findAll().forEach(cat ->
                        System.out.println(cat.getId() + " - " + cat.getNome())
                    );
                    break;

                case 3:
                    System.out.println("Escolhe a categoria:");
                    categoriaRepo.findAll().forEach(cat ->
                        System.out.println(cat.getId() + " - " + cat.getNome())
                    );

                    System.out.print("ID da categoria: ");
                    Long catId = Long.parseLong(sc.nextLine().trim());

                    Categoria categoria = categoriaRepo.findById(catId).orElse(null);
                    if (categoria == null) {
                        System.out.println("Categoria não existe.");
                        break;
                    }

                    Despesa d = new Despesa();
                    System.out.print("Descrição: ");
                    d.setDescricao(sc.nextLine());
                    System.out.print("Valor: ");
                    d.setValor(Double.parseDouble(sc.nextLine().trim()));
                    System.out.print("Data (YYYY-MM-DD): ");
                    d.setData(LocalDate.parse(sc.nextLine().trim()));
                    
                    System.out.println("Método de pagamento:");
                    System.out.println("1 - DINHEIRO");
                    System.out.println("2 - CARTAO");
                    System.out.println("3 - MBWAY");
                    System.out.println("4 - TRANSFERENCIA");
                    System.out.print("Escolha: ");
                    
                    int metodoOpcao = Integer.parseInt(sc.nextLine().trim());
                    MetodoPagamento metodo = switch (metodoOpcao) {
						case 1 -> MetodoPagamento.DINHEIRO;
						case 2 -> MetodoPagamento.CARTAO;
						case 3 -> MetodoPagamento.MBWAY;
						case 4 -> MetodoPagamento.TRANSFERENCIA_BANCARIA;
						default -> null;
						};
						
						if (metodo == null) {
							System.out.println("Método de pagamento inválido.");
							break;
						}
						
						d.setMetodoPagamento(metodo);
            
                    d.setCategoria(categoria);
                    despesaRepo.save(d);
                    System.out.println("OK! Despesa criada.");
                    break;
            			

                case 4:
                    System.out.println("Despesas:");
                    despesaRepo.findAll().forEach(dep ->
                        System.out.println(dep.getId() + " | " + dep.getDescricao() + " | " +
                                dep.getValor() + " | " + dep.getData() + " | " +
                                (dep.getCategoria() != null ? dep.getCategoria().getNome() : "-"))
                    );
                    break;

                case 5:
                    System.out.print("ID da Despesa a eliminar: ");
                    Long despesaId;
                    try {
                        despesaId = Long.parseLong(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("ID inválido.");
                        break;
                    }

                    if (!despesaRepo.existsById(despesaId)) {
                        System.out.println("Despesa não existe.");
                        break;
                    }

                    despesaRepo.deleteById(despesaId);
                    System.out.println("Despesa eliminada com sucesso.");
                    break;

                case 6:
                    List<Categoria> categorias = categoriaRepo.findAll();
                    if (categorias.isEmpty()) {
                        System.out.println("Não existem categorias para eliminar.");
                        break;
                    }

                    System.out.println("Categorias:");
                    categorias.forEach(cat ->
                        System.out.println(cat.getId() + " - " + cat.getNome())
                    );

                    System.out.print("ID da Categoria a eliminar: ");
                    Long categoriaId;
                    try {
                        categoriaId = Long.parseLong(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("ID inválido.");
                        break;
                    }

                    if (!categoriaRepo.existsById(categoriaId)) {
                        System.out.println("Categoria não existe.");
                        break;
                    }

                    if (despesaRepo.existsByCategoriaId(categoriaId)) {
                        System.out.println("Não é possível eliminar a categoria porque existem despesas associadas.");
                        break;
                    }

                    categoriaRepo.deleteById(categoriaId);
                    System.out.println("Categoria eliminada com sucesso.");
                    break;

                case 7:
                    List<Categoria> cats = categoriaRepo.findAll();
                    if (cats.isEmpty()) {
                        System.out.println("Não existem categorias.");
                        break;
                    }

                    System.out.println("Categorias:");
                    cats.forEach(cat ->
                        System.out.println(cat.getId() + " - " + cat.getNome())
                    );

                    System.out.print("ID da Categoria para listar despesas: ");
                    Long filtroId;
                    try {
                        filtroId = Long.parseLong(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("ID inválido.");
                        break;
                    }

                    List<Despesa> despesas = despesaRepo.findByCategoriaId(filtroId);

                    System.out.println("Despesas na categoria selecionada:");
                    if (despesas.isEmpty()) {
                        System.out.println("vazio");
                    } else {
                        despesas.forEach(dep ->
                            System.out.println(dep.getId() + " | " + dep.getDescricao() + " | " +
                                    dep.getValor() + " | " + dep.getData())
                        );
                    }
                    break;

                case 0:
                    System.out.println("A sair...");
                    sc.close();
                    return;

                default:
                    System.out.println("Opção inválida.");
            }
        }
    }
}
