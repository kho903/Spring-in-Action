package tacos.web;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import lombok.extern.slf4j.Slf4j;
import tacos.Order;
import tacos.User;
import tacos.data.OrderRepository;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {

	private OrderProps props;
	private OrderRepository orderRepository;

	public OrderController(OrderProps props, OrderRepository orderRepository) {
		this.props = props;
		this.orderRepository = orderRepository;
	}

	@GetMapping("/current")
	public String orderForm(@AuthenticationPrincipal User user,
			@ModelAttribute Order order) {

		if (order.getDeliveryName() == null) {
			order.setDeliveryName(user.getFullName());
		}
		if (order.getDeliveryStreet() == null) {
			order.setDeliveryStreet(user.getStreet());
		}
		if (order.getDeliveryCity() == null) {
			order.setDeliveryCity(user.getCity());
		}
		if (order.getDeliveryState() == null) {
			order.setDeliveryState(user.getState());
		}
		if (order.getDeliveryZip() == null) {
			order.setDeliveryZip(user.getZip());
		}

		return "orderForm";
	}

	@PostMapping
	public String processOrder(@Valid Order order, Errors errors,
			SessionStatus sessionStatus, @AuthenticationPrincipal User user) {
		if (errors.hasErrors()) {
			return "orderForm";
		}

		order.setUser(user);

		orderRepository.save(order);
		sessionStatus.setComplete();
		return "redirect:/";
	}

	@GetMapping
	public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
		Pageable pageable = PageRequest.of(0, props.getPageSize());
		model.addAttribute("orders", orderRepository.findByUserOrderByPlacedAtDesc(user, pageable));
		return "orderList";
	}

}
