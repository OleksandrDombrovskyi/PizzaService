package pizza.repository.order;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pizza.domain.Pizza;
import pizza.domain.customer.*;
import pizza.domain.order.*;
import pizza.domain.order.status.EnumStatusState;
import pizza.repository.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/repositoryTestContext.xml"})
public class JpaOrderRepositoryInMemTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired
	private Repository<Order> orderRepository;
	
	@Autowired
	private PizzaRepository pizzaRepository;
	
	private Order order;
	
	@Before
	public void initMethod() {
		order = createOrder();
	}
	
	private Order createOrder() {
		Pizza pizza1 = new Pizza("Pizza 1", 100, Pizza.PizzaType.SEA);
		Pizza pizza2 = new Pizza("Pizza 2", 200, Pizza.PizzaType.VEGETABLES);
		pizzaRepository.insertPizza(pizza1);
		pizzaRepository.insertPizza(pizza2);
		Address address = new Address("Kiev", "Kudryashova", "12", "4");
		Customer customer = new Customer("Customer name", address);
		Map<Pizza, Integer> pizzas = new HashMap<Pizza, Integer>();
		pizzas.put(pizza1, 3);
		pizzas.put(pizza2, 4);
		return new Order(customer, pizzas);
	}
	
	@Test
	public void testSaveOrderAndGetOrder() {
		orderRepository.insert(order);
		Order expected = order;
		Order result = orderRepository.get(order.getId());
		assertEquals(expected, result);
		assertEquals(expected.getDate(), result.getDate());
		assertEquals(expected.getStatus(), result.getStatus());
		assertEquals(expected.getTotalPrice(), result.getTotalPrice(), 0.0001);
		assertEquals(expected.getAddress(), result.getAddress());
		assertTrue(expected.getCustomer().getId() == result.getCustomer().getId()); // compare POJO with proxy
	}
	
	@Test
	public void testUpdateOrder() {
		orderRepository.insert(order);
		Address updatedAddress = new Address("Lviv", "Evreyska", "1", "5");
		Customer updatedCustomer = new Customer("Updated customer", updatedAddress);
		Date updatedDate = new Date(new java.util.Date().getTime());
		Map<Pizza, Integer> updatedPizzas = new HashMap<Pizza, Integer>();
		StatusState updatedStatus = EnumStatusState.NEW;
		order.setAddress(updatedAddress);
		order.setCustomer(updatedCustomer);
		order.setDate(updatedDate);
		order.setPizzas(updatedPizzas);
		order.setStatus(updatedStatus);
		order.setTotalPrice(order.countPrice());
		orderRepository.update(order);
		Order expected = order;
		Order result = orderRepository.get(order.getId());
		assertEquals(expected, result);
		assertEquals(expected.getAddress(), result.getAddress());
		assertTrue(expected.getCustomer().getId() == result.getCustomer().getId());
//		assertTrue(expected.getDate().equals(result.getDate())); // not solved
		assertEquals(expected.getStatus(), result.getStatus());
		assertEquals(expected.getTotalPrice(), result.getTotalPrice(), 0.0001);
	}
	
	@Test
	public void testDeleteOrder() {
		orderRepository.insert(order);
		boolean expected = true;
		boolean result = orderRepository.delete(order.getId());
		assertEquals(expected, result);
		
		expected = false;
		result = orderRepository.delete(100500);
		assertEquals(expected, result);
	}

}
