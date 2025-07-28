package com.mos.backend.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mos.backend.common.redis.RedisSubscriber;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoomInfo;
import com.mos.backend.studychatrooms.entity.StudyChatRoomInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;
    @Value("${spring.data.redis.private-chat-room-info-channel}")
    private String privateChatRoomInfoChannel;
    @Value("${spring.data.redis.study-chat-room-info-channel}")
    private String studyChatRoomInfoChannel;
    @Value("${spring.data.redis.private-chat-channel}")
    private String privateChatChannel;
    @Value("${spring.data.redis.study-chat-channel}")
    private String studyChatChannel;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisPort);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration);
        return lettuceConnectionFactory;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public RedisTemplate<String, PrivateChatRoomInfo> privateChatRoomInfoRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, PrivateChatRoomInfo> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<PrivateChatRoomInfo> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, PrivateChatRoomInfo.class);
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(Long.class));
        template.setHashValueSerializer(serializer);
        return template;
    }

    @Bean
    public RedisTemplate<String, StudyChatRoomInfo> studyChatRoomInfoRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, StudyChatRoomInfo> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<StudyChatRoomInfo> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, StudyChatRoomInfo.class);
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(Long.class));
        template.setHashValueSerializer(serializer);
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return template;
    }

    // refreshToken -> memberId
    // chatRoomId -> (userId1, userid2, ...)
    @Bean
    public RedisTemplate<String, Long> StringLongRedisTemplate() {
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Long.class));
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Map<String, String>> StringMapRedisTemplate() {
        RedisTemplate<String, Map<String, String>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Map.class));
        return redisTemplate;
    }


    @Bean
    public RedisMessageListenerContainer redisMessageListener(MessageListenerAdapter privateMessageListenerAdapter,
                                                              MessageListenerAdapter studyMessageListenerAdapter,
                                                              MessageListenerAdapter privateChatRoomInfoMessageListenerAdapter,
                                                              MessageListenerAdapter studyChatRoomInfoMessageListenerAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());

        container.addMessageListener(privateMessageListenerAdapter, new PatternTopic(privateChatChannel));
        container.addMessageListener(studyMessageListenerAdapter, new PatternTopic(studyChatChannel));
        container.addMessageListener(privateChatRoomInfoMessageListenerAdapter, new PatternTopic(privateChatRoomInfoChannel));
        container.addMessageListener(studyChatRoomInfoMessageListenerAdapter, new PatternTopic(studyChatRoomInfoChannel));

        return container;
    }

    @Bean
    public MessageListenerAdapter privateMessageListenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onPrivateChatMessage");
    }

    @Bean
    public MessageListenerAdapter studyMessageListenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onStudyChatMessage");
    }

    @Bean
    public MessageListenerAdapter privateChatRoomInfoMessageListenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onPrivateChatRoomInfoMessage");
    }

    @Bean
    public MessageListenerAdapter studyChatRoomInfoMessageListenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onStudyChatRoomInfoMessage");
    }
}
