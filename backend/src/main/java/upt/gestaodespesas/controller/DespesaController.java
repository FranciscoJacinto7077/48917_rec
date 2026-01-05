package upt.gestaodespesas.controller;

import java.util.List;

import javax.validation.Valid;

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
	
	// Obter despesa por ID
	@GetMapping("/{id}")
	public ResponseEntity<Despesa> obterDespesaPorId(@PathVariable Long id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	// Criar nova despesa
	@PostMapping
	public ResponseEntity<Despesa> criarDespesa(@Valid @RequestBody Despesa despesa) {
	    despesa.setId(null);
	    Despesa guardada = repo.save(despesa);
	    return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
	
	}
	
	// Atualizar despesa existente
	@PutMapping("/{id}")
	public ResponseEntity<Despesa> atualizarDespesa(@PathVariable Long id, @Valid @RequestBody Despesa dados) {
		return repo.findById(id)
				.map(existingDespesa -> {
					existingDespesa.setDescricao(dados.getDescricao());
					existingDespesa.setValor(dados.getValor());
					existingDespesa.setData(dados.getData());
					Despesa atualizada = repo.save(existingDespesa);
					return ResponseEntity.ok(atualizada);
				})
				.orElse(ResponseEntity.notFound().build());
	}
	
	// Deletar despesa
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> apagarDespesa(@PathVariable Long id) {
	    if (!repo.existsById(id)) {
	        return ResponseEntity.notFound().build();
	    }
	    repo.deleteById(id);
	    return ResponseEntity.noContent().build();
	}

}
