package com.shorter_url.shorter_service;

import com.shorter_url.shorter_service.service.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RateLimitServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        rateLimitService = new RateLimitService(redisTemplate);
    }

    @Test
    void shouldNotBlockFirstRequest() {
        // given
        String ip = "192.168.1.1";

        when(valueOperations.increment("rate_limit:" + ip))
                .thenReturn(1L);

        // when
        boolean result = rateLimitService.isRateLimited(ip);

        // then
        assertFalse(result);

        verify(redisTemplate)
                .expire("rate_limit:" + ip, Duration.ofMinutes(1));
    }

    @Test
    void shouldBlockEleventhRequest() {
        // given
        String ip = "192.168.1.1";

        when(valueOperations.increment("rate_limit:" + ip))
                .thenReturn(11L);

        // when
        boolean result = rateLimitService.isRateLimited(ip);

        // then
        assertTrue(result);

        verify(redisTemplate, never())
                .expire(anyString(), any());
    }

    @Test
    void shouldSetTtlOnFirstRequest() {
        // given
        String ip = "192.168.1.1";

        when(valueOperations.increment("rate_limit:" + ip))
                .thenReturn(1L);

        // when
        rateLimitService.isRateLimited(ip);

        // then
        verify(redisTemplate)
                .expire("rate_limit:" + ip, Duration.ofMinutes(1));
    }

    @Test
    void shouldNotResetTtlOnSubsequentRequests() {
        // given
        String ip = "192.168.1.1";

        when(valueOperations.increment("rate_limit:" + ip))
                .thenReturn(2L);

        // when
        rateLimitService.isRateLimited(ip);

        // then
        verify(redisTemplate, never())
                .expire(anyString(), any());
    }

}
