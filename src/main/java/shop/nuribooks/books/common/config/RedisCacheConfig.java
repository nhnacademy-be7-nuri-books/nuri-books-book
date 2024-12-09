package shop.nuribooks.books.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Profile("dev")
@Configuration
public class RedisCacheConfig {

	// 레디스 캐시 매니저
	@Bean("redisCacheManager")
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		return RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(connectionFactory)
			.cacheDefaults(defaultConfiguration())
			.withInitialCacheConfigurations(configureMap())
			.build();
	}

	// 캐시 기본설정
	private RedisCacheConfiguration defaultConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig()
			.disableCachingNullValues()
			.entryTtl(Duration.ofMinutes(5))
			//			.computePrefixWith(CacheKeyPrefix.prefixed("cache:"))  // 여기서 접두사 지정
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
	}

	// 각 캐시별로 TTL(Time-To-Live) 적절히 설정
	private Map<String, RedisCacheConfiguration> configureMap() {
		Map<String, RedisCacheConfiguration> cacheConfigurationMap = new HashMap<>();
		cacheConfigurationMap.put("topBookLikeCache", defaultConfiguration().entryTtl(Duration.ofHours(6)));
		cacheConfigurationMap.put("topBookScoreCache", defaultConfiguration().entryTtl(Duration.ofHours(6)));
		cacheConfigurationMap.put("bookCategoryCache", defaultConfiguration().entryTtl(Duration.ofDays(1)));
		return cacheConfigurationMap;
	}
}
