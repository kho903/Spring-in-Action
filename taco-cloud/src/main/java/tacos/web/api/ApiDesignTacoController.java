package tacos.web.api;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tacos.Taco;
import tacos.data.TacoRepository;

@RestController
@RequestMapping(path = "/design", produces = "application/json")
@CrossOrigin(origins = "*")
public class ApiDesignTacoController {
	private TacoRepository tacoRepository;

	public ApiDesignTacoController(TacoRepository tacoRepository) {
		this.tacoRepository = tacoRepository;
	}

	@GetMapping("/recent")
	public Iterable<Taco> recentTacos() {
		PageRequest	page = PageRequest.of(
			0, 12, Sort.by("createdAt").descending());
		return tacoRepository.findAll(page).getContent();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
		Optional<Taco> optionalTaco = tacoRepository.findById(id);
		if (optionalTaco.isPresent()) {
			return new ResponseEntity<>(optionalTaco.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	@PostMapping(consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Taco PostTaco(@RequestBody Taco taco) {
		return tacoRepository.save(taco);
	}

}
