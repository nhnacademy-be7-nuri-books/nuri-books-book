package shop.nuribooks.books.repository.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.member.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByEmail(String email);

	boolean existsByPassword(String encryptedPassword);

	List<Customer> findAllByPassword(String encryptedPassword);
}
