package shop.nuribooks.books.dto.publisher;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PublisherRequest(@NotNull String name) {
}
