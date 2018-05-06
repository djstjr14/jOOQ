package me.sangwon;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import me.sangwon.dto.CustomerDTO;
import me.sangwon.repository.CustomerRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootJooqExampleApplicationTests6 {
   @Autowired
   private CustomerRepository customerRepository;
   private CustomerRepository customerTest;

	//test for method_ void save() 
   @Test
   public void save() {
      customerRepository.save("hello", "kitty@yaho.com");
      final CustomerDTO customerDTO = customerRepository.findByname("hello").stream().findFirst().get();
      assertEquals(customerDTO.getName(),"hello");
   }

	@Test (expected=DataIntegrityViolationException.class)
	public void test_save_1(){ // input invalid value.
		customerRepository.save(null, "ch02@naver.com");  // name=null. expected error?
		customerRepository.save("choyoung", null);  // email=null. expected error?
		customerRepository.save(null, null);  // both=null. expected error?

	} // expecting error. becuz, name field can't be null.
	//or it will input "customer" automatically or it will be succeeded? 


	@Test
	public void test_save_2() { // input two obejects which have same name and email.
		customerRepository.save("cho02", "ch02@naver.com");
		customerRepository.save("cho02", "ch02@naver.com"); // expected error? 
		final CustomerDTO customer_1 = customerRepository.findByname("cho02").stream().findFirst().get();
		final CustomerDTO customer_2 = customerRepository.findByname("cho02").stream().findFirst().get();
		assertEquals(customer_1.getId(), customer_2.getId());
	}

	 // input two obejects which have different name but same email.
	@Test
	public void test_save_3() {
		customerRepository.save("hello", "ch02@naver.com");
		customerRepository.save("kitty", "ch02@naver.com");
		final CustomerDTO customer_1 = customerRepository.findByname("hello").stream().findFirst().get();
		final CustomerDTO customer_2 = customerRepository.findByname("kitty").stream().findFirst().get();
		// assertEquals(customer_1.getId(), customer_2.getId()); // different result
		assertEquals(customer_1.getEmail(), customer_2.getEmail());
	}

	@Test
	public void test_save_4() { // input two obejects which have same name but different email.
		customerRepository.save("hello", "ch02@naver.com");
		customerRepository.save("kitty", "ch02@naver.com"); // expected error? 
		final CustomerDTO customer_1 = customerRepository.findByname("cho02").stream().findFirst().get();
		final CustomerDTO customer_2 = customerRepository.findByname("cho02").stream().findFirst().get();
		assertEquals(customer_1.getId(), customer_2.getId());
		assertEquals(customer_1.getEmail(), customer_2.getEmail());
	}


	//test for method_ void findOne(Integer)
	@Test (expected=NoSuchElementException.class)
	public void test_findOne_1() { // find id which doesnt exists 
		final CustomerDTO customerDTO = customerRepository.findOne(50).get(); //expected error 
	}

	@Test
	public void test_findOne_2() { // find objects which have same id. then, compare their name. 
		final CustomerDTO customer_1 = customerRepository.findOne(3).get();
		final CustomerDTO customer_2 = customerRepository.findOne(3).get();
		assertEquals(customer_1.getName(), customer_2.getName());
	}
	
	@Test (expected=NoSuchElementException.class)
	public void test_findByname() { //find with value of 'null' 
		customerRepository.findByname(null).stream().findFirst().get(); //expected error.
	}
		
}
