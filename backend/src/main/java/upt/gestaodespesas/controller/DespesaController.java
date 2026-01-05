package upt.gestaodespesas.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.model.Despesa;
import upt.gestaodespesas.repository.DespesaRepository;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {
	
	private final DespesaRepository repo;
	
	public DespesaController(DespesaRepository repo) {
		this.repo = repo;
	}

	// Listar todas as despesas
	@GetMapping
	public List<Despesa> listarDespesas() {
		return repo.findAll();
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Despesa> obterDespesaPorId(@PathVariable Long id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Despesa criarDespesa(@RequestBody Despesa despesa) {
		despesa.setId(null); // Garantir que o ID seja nulo para criação
		return repo.save(despesa);
	}
}
