package shop.nuribooks.books.common.message;

import lombok.Builder;

@Builder
public record ResponseMessage (Integer statusCode, String message){
}
