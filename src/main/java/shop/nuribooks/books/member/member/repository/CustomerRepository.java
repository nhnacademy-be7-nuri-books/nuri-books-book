package shop.nuribooks.books.member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.member.member.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByEmail(String email);

	boolean existsByIdAndPassword(Long id, String password);
}
