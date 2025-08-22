package com.pei.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pei.dto.Alert;
import com.pei.dto.Logins;
import com.pei.repository.LoginsRepository;

@Service
public class GeolocalizationService {
    
    private LoginsRepository loginsRepository;
    private GeoSimService geoSimService;

    public GeolocalizationService(GeoSimService geoSimService, LoginsRepository loginsRepository) {
        this.geoSimService = geoSimService;
        this.loginsRepository = loginsRepository;
    }
    
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
    
    public Alert verifyFraudOfDeviceAndGeolocation(Logins login) {
        
        String countryActual = geoSimService.getCountryFromIP(login.country());

        List<Logins> loginsDelUser = loginsRepository.findLoginsByUserAndCountryAndDevice(login.userId(),
                countryActual, login.deviceID(), true);

        List<Logins> allLogins = loginsRepository.findAll();
        Long lastLoginsId = allLogins.isEmpty() ? 1 : allLogins.get(allLogins.size() - 1).id() + 1;

        loginsRepository.save(new Logins(lastLoginsId, login.userId(), login.deviceID(), login.country(),
                LocalDateTime.now(), false));

        boolean loginsMatch = loginsDelUser.isEmpty();
        
        if (loginsMatch) {
            return new Alert(login.userId(), "Device and geolocalization problem detected for " + login.userId());
        }
        return new Alert(login.userId(), "Something else");
    }
}
