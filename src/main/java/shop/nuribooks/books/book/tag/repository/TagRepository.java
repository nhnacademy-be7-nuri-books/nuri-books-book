package shop.nuribooks.books.book.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
	boolean existsByName(String name);
}
