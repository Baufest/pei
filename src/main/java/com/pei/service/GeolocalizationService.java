package com.pei.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pei.dto.Alert;
import com.pei.dto.Logins;
import com.pei.repository.LoginsRepository;

@Service
public class GeolocalizationService {
    
    private LoginsRepository loginsRepository;

    @Autowired
    public GeolocalizationService(LoginsRepository loginsRepository) {
        this.loginsRepository = loginsRepository;}
    
    public Alert getLoginAlert(Long userId) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Logins> loginsRecientes = loginsRepository.findRecentLogins(userId, oneHourAgo);

        Set<String> paises = loginsRecientes.stream()
                .map(Logins::getCountry)
                .collect(Collectors.toSet());

        if (paises.size() >= 2) {
            return new Alert(userId, "Multiple countries logins detected for user " + userId);
        }

        return null; 
    }
    
    
    }
