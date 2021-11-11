package com.payout.transfert.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.payout.transfert.dao.Resource;

@Component
public class TokenDataService {
 
    @Cacheable(value = "tokens", key="#resource.operatorCode")
    public String getToken(String token, Resource resource) {
        return resource.getCacheToken();
    }
}
