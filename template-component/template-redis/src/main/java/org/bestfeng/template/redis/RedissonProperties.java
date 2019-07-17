package org.bestfeng.template.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.bean.FastBeanCopier;
import org.redisson.config.Config;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;

import java.util.Map;

/**
 * @author zhouhao
 * @since 1.1.0
 */
@Getter
@Setter
@Slf4j
public class RedissonProperties {
    private String[] hosts;

    private int database = 0;

    private String password;

    private int connectionPoolSize = 128;

    private int connectionTimeout = 10000;

    private int timeout = 10000;

    private String masterName = "iot-redis-cluster";

    private Map<String, Object> config;

    private Type type = Type.single;


    public Config toConfig(RedissonProperties defaultProperties) {
        if (this.hosts == null) {
            this.hosts = defaultProperties.hosts;
        }
        if (this.password == null) {
            this.password = defaultProperties.password;
        }

        return  type.parse(this);
    }

    private <T> T copyConfig(T object) {
        if (this.config != null) {
            FastBeanCopier.copy(this.config, object);
        }
        return object;
    }

    public enum Type {
        single {
            @Override
            Config parse(RedissonProperties properties) {
                Config config = new Config();
                SingleServerConfig serversConfig = config.useSingleServer()
                        .setAddress(properties.getHosts()[0])
                        .setPassword(properties.getPassword())
                        .setConnectionPoolSize(properties.getConnectionPoolSize())
                        .setConnectTimeout(properties.getConnectionTimeout())
                        .setKeepAlive(true)
                        .setRetryInterval(100)
                        .setConnectionMinimumIdleSize(16)
                        .setRetryAttempts(500)
                        .setTimeout(properties.timeout)
                        .setDatabase(properties.getDatabase());
                properties.copyConfig(serversConfig);


                if (log.isInfoEnabled()) {
                    log.info("redisson SingleServerConfig config:\n{}", JSON.toJSONString(serversConfig, SerializerFeature.PrettyFormat));
                }
                return config;
            }
        }, cluster {
            @Override
            Config parse(RedissonProperties properties) {
                Config config = new Config();
                String[] hosts = properties.getHosts();
                String master = hosts[0];
                String[] slave = new String[hosts.length - 1];
                System.arraycopy(hosts, 1, slave, 0, slave.length);
                MasterSlaveServersConfig serversConfig = config.useMasterSlaveServers()
                        .setMasterAddress(master)
                        .addSlaveAddress(slave)
                        .setMasterConnectionPoolSize(properties.getConnectionPoolSize())
                        .setSlaveConnectionPoolSize(properties.getConnectionPoolSize())
                        .setConnectTimeout(properties.getConnectionTimeout())
                        .setTimeout(properties.timeout)
                        .setKeepAlive(true)
                        .setRetryInterval(100)
                        .setRetryAttempts(500)
                        .setPassword(properties.getPassword())
                        .setDatabase(properties.getDatabase());
                properties.copyConfig(serversConfig);

                if (log.isInfoEnabled()) {
                    log.info("redisson MasterSlaveServersConfig config:\n{}", JSON.toJSONString(serversConfig, SerializerFeature.PrettyFormat));
                }
                return config;
            }
        }, sentinel {
            @Override
            Config parse(RedissonProperties properties) {
                Config config = new Config();
                String[] hosts = properties.getHosts();
                SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                        .setScanInterval(1000)
                        .setPingConnectionInterval(1000)
                        .setFailedSlaveCheckInterval(5000)
                        .setFailedSlaveReconnectionInterval(3000)
                        .setDnsMonitoringInterval(1000)
                        .setKeepAlive(true)
                        .addSentinelAddress(hosts)
                        .setMasterConnectionPoolSize(properties.getConnectionPoolSize())
                        .setSlaveConnectionPoolSize(properties.getConnectionPoolSize())
                        .setConnectTimeout(properties.getConnectionTimeout())
                        .setTimeout(properties.timeout)
                        .setRetryInterval(100)
                        .setRetryAttempts(500)
                        .setMasterName(properties.getMasterName())
                        .setPassword(properties.getPassword())
                        .setDatabase(properties.getDatabase());
                properties.copyConfig(sentinelServersConfig);

                if (log.isInfoEnabled()) {
                    log.info("redisson SentinelServersConfig config:\n{}", JSON.toJSONString(sentinelServersConfig, SerializerFeature.PrettyFormat));
                }
                return config;
            }
        };

        abstract Config parse(RedissonProperties properties);
    }
}
