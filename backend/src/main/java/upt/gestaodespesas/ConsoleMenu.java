package upt.gestaodespesas;

import java.time.LocalDate;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import upt.gestaodespesas.model.Categoria;
import upt.gestaodespesas.model.Despesa;
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