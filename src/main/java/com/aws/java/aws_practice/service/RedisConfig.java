package com.aws.java.aws_practice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ObjectUtils;


@Configuration
public class RedisConfig {
    @Value("${spring.redis.host:localhost}")
    private String redisHost;
    @Value("${spring.redis.port:6379}")
    private int redisPort;
    @Value("${spring.redis.password:}")
    private String redisPassword;
    private static final Logger LOGGER = LogManager.getLogger(RedisConfig.class);

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        LOGGER.info("Connection to redis");
        if("localhost".equalsIgnoreCase(redisHost)) {
            LOGGER.info("connection to redis host using standalone configuration:{}",redisHost);
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
            configuration.setHostName(redisHost);
            configuration.setPort(redisPort);
            return new LettuceConnectionFactory(configuration);
        }else{
            LOGGER.info("connection to redis host using cluster configuration:{}",redisHost);
            RedisClusterConfiguration redisClusterConfiguration=new RedisClusterConfiguration();

            redisClusterConfiguration.clusterNode(redisHost,redisPort);
            LettuceConnectionFactory lcf=new LettuceConnectionFactory(redisClusterConfiguration);
            if(!ObjectUtils.isEmpty(redisPassword)) {
                redisClusterConfiguration.setPassword(redisPassword);
                LettuceClientConfiguration lcc=LettuceClientConfiguration.builder().useSsl().disablePeerVerification().build();
                lcf=new LettuceConnectionFactory(redisClusterConfiguration,lcc);
            }
            LOGGER.info("REDIS SSL:{} HOST VERIFY:{}",lcf.getClientConfiguration().isUseSsl(),lcf.getClientConfiguration().isVerifyPeer());
            return lcf;
        }
    }

    @Bean
    @Primary
    public RedisTemplate<String, String> template() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new JdkSerializationRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
}
