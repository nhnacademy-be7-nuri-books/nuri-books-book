package shop.nuribooks.books.member.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.customer.entity.Customer;

/**
 * @author Jprotection
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByEmail(String email);

	boolean existsByIdAndPassword(Long id, String password);
}
