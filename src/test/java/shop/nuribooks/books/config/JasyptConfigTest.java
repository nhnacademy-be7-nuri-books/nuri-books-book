package shop.nuribooks.books.config;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class JasyptConfigTest {
	@Autowired
	@Qualifier("jasyptStringEncryptor")
	StringEncryptor encryptor;
	@Autowired
	private Environment env;

	// @Test
	// void test() {
	// 	encryptInfos("MYSQL_URL");
	// 	encryptInfos("MYSQL_DEV_URL");
	// 	encryptInfos("MYSQL_ID");
	// 	encryptInfos("MYSQL_PWD");
	// }

	/*@Test
	void encryptAladinApiKey() {
		encryptInfos("aladin.api.key");
	}*/

	private String encryptInfos(String key) {
		String value = env.getProperty(key);
		String encVal = encryptor.encrypt(value);
		log.info("{} : {}", key, encVal);
		log.info("{}", encryptor.decrypt(encVal));
		return encVal;
	}
}