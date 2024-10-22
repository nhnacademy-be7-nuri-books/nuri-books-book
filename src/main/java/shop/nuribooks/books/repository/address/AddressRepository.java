package shop.nuribooks.books.repository.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.nuribooks.books.entity.address.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
