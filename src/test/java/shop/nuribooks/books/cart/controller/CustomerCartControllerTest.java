package shop.nuribooks.books.cart.controller;

class CustomerCartControllerTest {

	//    @Autowired
	//    private MockMvc mockMvc;
	//
	//    @Autowired
	//    ObjectMapper objectMapper;
	//
	//    @DisplayName("비회원 장바구니에 도서를 담는다.")
	//    @Test
	//    void addCustomerCart() throws Exception {
	//        // given
	//        CartAddRequest request = new CartAddRequest( 1L, 1);
	//
	//        // when then
	//        mockMvc.perform(post("/api/customer/cart")
	//                        .content(objectMapper.writeValueAsString(request))
	//                        .contentType(MediaType.APPLICATION_JSON)
	//                )
	//                .andExpect(status().isOk());
	//    }
	//
	//    @DisplayName("비회원 장바구니 리스트를 가져온다")
	//    @Test
	//    void getCustomerCartList() throws Exception {
	//        // given
	//        MockHttpSession session = new MockHttpSession();
	//        Long bookId1 = 1L;
	//        List<String> tagNames = new ArrayList<>();
	//        tagNames.add("wow");
	//
	//        BigDecimal price = BigDecimal.valueOf(10000);
	//        int discountRate = 10;
	//        BigDecimal salePrice = price
	//            .multiply(BigDecimal.valueOf(100 - discountRate))
	//            .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
	//
	//        Map<String, List<String>> contributorsByRole = new HashMap<>();
	//        contributorsByRole.put("지은이", List.of("유재령", "이영애", "차효정"));
	//        contributorsByRole.put("감수", List.of("김광웅"));
	//
	//        BookResponse bookResponse1 = new BookResponse(
	//            bookId1, null, "정상", "책 제목", "thumbnail.jpg", null, LocalDate.now(),
	//            price, discountRate, salePrice, "책 설명", "책 내용", "1234567890123",
	//            true, 0, 10, 100L, tagNames, contributorsByRole);
	//
	//        CartResponse response1 = new CartResponse(bookResponse1, 1);
	//
	//        Long bookId2 = 2L;
	//        BookResponse bookResponse2 = new BookResponse(
	//            bookId2, null, "정상", "책 제목", "thumbnail.jpg", null, LocalDate.now(),
	//            price, discountRate, salePrice, "책 설명", "책 내용", "1234567890123",
	//            true, 0, 10, 100L, tagNames, contributorsByRole);
	//
	//        CartResponse response2 = new CartResponse(bookResponse2, 2);
	//        when(customerCartService.getCustomerCartList(anyString())).thenReturn(List.of(response1, response2));
	//
	//        // when then
	//        mockMvc.perform(get("/api/customer/cart")
	//                        .session(session))
	//                .andExpect(status().isOk())
	//                .andExpect(jsonPath("$.*", hasSize(2)));
	//
	//    }
	//
	//    @DisplayName("비회원 장바구니를 삭제한다")
	//    @Test
	//    void removeCustomerCart() throws Exception {
	//        // given
	//        MockHttpSession session = new MockHttpSession();
	//        // when then
	//        mockMvc.perform(delete("/api/customer/cart")
	//                        .session(session))
	//                .andExpect(status().isNoContent());
	//    }
	//
	//    @DisplayName("비회원 장바구니에서 특정 아이템을 삭제한다")
	//    @Test
	//    void removeCustomerCartItem() throws Exception {
	//        // given
	//        MockHttpSession session = new MockHttpSession();
	//        //when then
	//        mockMvc.perform(delete("/api/customer/cart/{bookId}", 1L)
	//                        .session(session))
	//                .andExpect(status().isNoContent());
	//    }

}
