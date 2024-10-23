-- 애플리케이션 실행 시 초기 데이터 삽입
-- 회원 상태, 권한, 등급 초기화
-- INSERT IGNORE는 중복된 키가 존재할 경우, 해당 행을 삽입하지 않고 오류없이 무시

-- INSERT IGNORE INTO status (status_id, name) VALUES (1, 'ACTIVE');
-- INSERT IGNORE INTO status (status_id, name) VALUES (2, 'INACTIVE');
-- INSERT IGNORE INTO status (status_id, name) VALUES (3, 'WITHDRAWN');
--
-- INSERT IGNORE INTO authority (authority_id, name) VALUES (1, 'ADMIN');
-- INSERT IGNORE INTO authority (authority_id, name) VALUES (2, 'MEMBER');
-- INSERT IGNORE INTO authority (authority_id, name) VALUES (3, 'SELLER');

-- INSERT IGNORE INTO grade (grade_id, name, point_rate, requirement) VALUES (1, 'STANDARD', , );
-- INSERT IGNORE INTO grade (grade_id, name, point_rate, requirement) VALUES (2, 'GOLD', , );
-- INSERT IGNORE INTO grade (grade_id, name, point_rate, requirement) VALUES (3, 'PLATINUM', , );
-- INSERT IGNORE INTO grade (grade_id, name, point_rate, requirement) VALUES (4, 'ROYAL', , );