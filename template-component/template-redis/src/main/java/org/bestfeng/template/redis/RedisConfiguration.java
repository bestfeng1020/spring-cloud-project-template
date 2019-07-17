package org.bestfeng.template.redis;

import org.hswebframework.web.authorization.token.DefaultUserTokenManager;
import org.hswebframework.web.authorization.token.SimpleUserToken;
import org.hswebframework.web.authorization.token.UserToken;
import org.hswebframework.web.authorization.token.UserTokenManager;
import org.hswebframework.web.concurrent.counter.BloomFilterManager;
import org.hswebframework.web.counter.redis.RedisBloomFilterManager;
import org.jetlinks.core.ProtocolSupports;
import org.jetlinks.core.device.registry.DeviceMessageHandler;
import org.jetlinks.core.message.interceptor.DeviceMessageSenderInterceptor;
import org.jetlinks.registry.redis.RedissonDeviceMessageHandler;
import org.jetlinks.registry.redis.RedissonDeviceRegistry;
import org.nustaq.serialization.FSTConfiguration;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.redisson.codec.FstCodec;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouhao
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(MultiRedissonProperties.class)
public class RedisConfiguration {

    @Value("${iot.redis.user-token-client-name:defaultRedissonClient}")
    private String userTokenClientName = "defaultRedissonClient";

    @Bean
    @Primary
    public RedissonClient defaultRedissonClient(RedissonClientRepository repository) {
        return repository.getDefaultClient();
    }

    @Bean
    public BloomFilterManager bloomFilterManager(RedissonClientRepository repository) {
        return new RedisBloomFilterManager(repository.getClient("bloom-filter")
                .orElseGet(repository::getDefaultClient));
    }

    @Bean
    public RedissonClientRepository redissonClientRepository() {
        DefaultRedissonClientRepository repository = new DefaultRedissonClientRepository();

        repository.setCodec(fstCodec());

        return repository;
    }

    @Bean
    public Codec fstCodec() {
        FSTConfiguration def = FSTConfiguration.createDefaultConfiguration();
        def.setClassLoader(this.getClass().getClassLoader());
        def.setForceSerializable(true);
        StringCodec stringCodec = new StringCodec();
        return new FstCodec(def) {
            @Override
            public Decoder<Object> getMapKeyDecoder() {
                return stringCodec.getMapKeyDecoder();
            }

            @Override
            public Encoder getMapKeyEncoder() {
                return stringCodec.getMapKeyEncoder();
            }
        };
    }

    @Bean
    public CacheManager cacheManager(RedissonClientRepository repository) {
        RedissonClient redissonClient = repository.getDefaultClient();
        LocalCachedMapOptions<Object, Object> localCachedMapOptions =
                LocalCachedMapOptions.defaults()
                        .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LFU)
                        .syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE)
                        .maxIdle(30, TimeUnit.MINUTES)
                        .timeToLive(30, TimeUnit.MINUTES)
                        .cacheSize(2048);
        Codec codec = fstCodec();
        RedissonSpringCacheManager cacheManager = new RedissonSpringCacheManager(redissonClient) {
            @Override
            protected RMapCache<Object, Object> getMapCache(String name, CacheConfig config) {
                return redissonClient.getMapCache(name, codec, localCachedMapOptions);
            }
        };
        cacheManager.setCodec(fstCodec());
        return new TransactionAwareCacheManagerProxy(cacheManager) {
            @Override
            public Cache getCache(String name) {
                return new AutoClearCache(super.getCache(name));
            }
        };
    }

    @Bean
    @ConditionalOnProperty(prefix = "iot.redis.user-token", name = "enable", havingValue = "true", matchIfMissing = true)
    @ConfigurationProperties(prefix = "hsweb.authorize")
    public UserTokenManager userTokenManager(RedissonClientRepository repository) {
        LocalCachedMapOptions<String, SimpleUserToken> localCachedMapOptions =
                LocalCachedMapOptions.<String, SimpleUserToken>defaults()
                        .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LFU)
                        .syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE)
                        .maxIdle(30, TimeUnit.MINUTES)
                        .timeToLive(30, TimeUnit.MINUTES)
                        .cacheSize(2048);
        Codec codec = fstCodec();
        RedissonClient client = repository.getClient(userTokenClientName).orElseGet(repository::getDefaultClient);
        ConcurrentMap<String, SimpleUserToken> repo = client.getMap("iot.user-token", codec, localCachedMapOptions);
        ConcurrentMap<String, Set<String>> userRepo = client.getMap("iot.user-token-user", codec);

        return new DefaultUserTokenManager(repo, userRepo) {
            @Override
            protected Set<String> getUserToken(String userId) {
                userRepo.computeIfAbsent(userId, u -> new HashSet<>());
                return client.getSet("iot.user-token-" + userId, codec);
            }

            @Override
            protected void syncToken(UserToken userToken) {
                tokenStorage.put(userToken.getToken(), (SimpleUserToken) userToken);
            }
        };
    }

   /* @Bean
    public RedissonDeviceMessageHandler deviceMessageHandler(RedissonClientRepository repository) {
        return new RedissonDeviceMessageHandler(repository.getClient("device-registry")
                .orElseGet(repository::getDefaultClient));
    }*/

    /*@Bean
    public RedissonDeviceRegistry deviceRegistry(RedissonClientRepository repository,
                                                 DeviceMessageHandler messageHandler,
                                                 ProtocolSupports protocolSupports) {

        return new RedissonDeviceRegistry(
                repository.getClient("device-registry").orElseGet(repository::getDefaultClient),
                messageHandler,
                protocolSupports);
    }*/

   /* @Bean
    public BeanPostProcessor deviceMessageSenderAutoRegister(RedissonDeviceRegistry registry) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
                return o;
            }

            @Override
            public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
                if (o instanceof DeviceMessageSenderInterceptor) {
                    registry.addInterceptor(((DeviceMessageSenderInterceptor) o));
                }
                return o;
            }
        };
    }*/
}
