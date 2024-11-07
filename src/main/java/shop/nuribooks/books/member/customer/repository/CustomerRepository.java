package shop.nuribooks.books.member.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.customer.entity.Customer;

/**
 * @author Jprotection
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	Optional<Customer> findByEmail(String email);

	boolean existsByIdAndPassword(Long id, String password);

	Optional<Customer> findByIdAndPassword(Long id, String password);
}
