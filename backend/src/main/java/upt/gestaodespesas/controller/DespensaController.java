package upt.gestaodespesas.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import upt.gestaodespesas.model.Despesa;
import upt.gestaodespesas.repository.DespesaRepository;

@RestController
@RequestMapping("/api/despesas")
public class DespensaController {
	
	private final DespesaRepository repo;
	
	public DespensaController(DespesaRepository repo) {
		this.repo = repo;
	}
	
	@GetMapping
	public List<Despesa> listarDespesas() {
		return repo.findAll();
		
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Despesa criarDespesa(@RequestBody Despesa despesa) {
		despesa.setId(null); // Garantir que o ID seja nulo para criação
		return repo.save(despesa);
	}
}
