package tacos.web;

import static tacos.Ingredient.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import tacos.Ingredient;
import tacos.Taco;
import tacos.data.IngredientRepository;

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {

	private final IngredientRepository ingredientRepository;

	@Autowired
	public DesignTacoController(IngredientRepository ingredientRepository) {
		this.ingredientRepository = ingredientRepository;
	}

	@GetMapping
	public String showDesignForm(Model model) {
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepository.findAll().forEach(i -> ingredients.add(i));

		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(),
				filterByType(ingredients, type));
		}

		model.addAttribute("taco", new Taco());
		return "design";
	}

	private List<Ingredient> filterByType(
		List<Ingredient> ingredients, Type type) {
		return ingredients
			.stream()
			.filter(x -> x.getType().equals(type))
			.collect(Collectors.toList());
	}

	@PostMapping
	public String processDesign(@Valid Taco design, Errors errors) {
		if (errors.hasErrors()) {
			return "design";
		}
		// TODO: 이 지점에서 타코 디자인(선택된 식자재 내역)을 저장.
		log.info("Processing design: " + design);

		return "redirect:/orders/current";
	}
}
