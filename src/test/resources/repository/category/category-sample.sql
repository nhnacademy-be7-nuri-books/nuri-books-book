INSERT INTO categories (level, name)
VALUES (0, '여행'),
       (0, '음식'),
       (0, '문학'),
       (0, '스포츠'),
       (0, '과학');

INSERT INTO categories (level, name, parent_id)
VALUES (1, '국내 여행', 1),
       (1, '해외 여행', 1),
       (1, '한식', 2),
       (1, '양식', 2),
       (1, '중식', 2),
       (1, '소설', 3),
       (1, '시', 3),
       (1, '축구', 4),
       (1, '야구', 4),
       (1, '물리학', 5),
       (2, '서울', 6),
       (2, '부산', 6),
       (2, '유럽 여행', 7),
       (2, '아시아 여행', 7),
       (2, '비빔밥', 8),
       (2, '불고기', 8),
       (2, '파스타', 9),
       (2, '스테이크', 9),
       (2, '탕수육', 10),
       (2, '짜장면', 10);