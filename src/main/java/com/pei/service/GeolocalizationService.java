package com.pei.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pei.dto.Alert;
import com.pei.domain.Login;
import com.pei.repository.LoginRepository;

@Service
public class GeolocalizationService {
    
    private LoginRepository loginRepository;
    private GeoSimService geoSimService;


    public GeolocalizationService(GeoSimService geoSimService, LoginRepository loginRepository) {
        this.geoSimService = geoSimService;
        this.loginRepository = loginRepository;
    }
    
    public Alert getLoginAlert(Long userId) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Login> LoginRecientes = loginRepository.findRecentLogin(userId, oneHourAgo);

        Set<String> paises = LoginRecientes.stream()
                .map(Login::getCountry)
                .collect(Collectors.toSet());

        if (paises.size() >= 2) {
            return new Alert(userId, "Multiple countries Login detected for user " + userId);
        }

        return null; 
    }
    
    public Alert verifyFraudOfDeviceAndGeolocation(Login login) {
        
        String countryActual = geoSimService.getCountryFromIP(login.getCountry());

        List<Login> LoginDelUser = loginRepository.findLoginByUserAndCountryAndDevice(login.getUser().getId(),
                countryActual, login.getDevice().getDeviceId(), true);

        List<Login> allLogin = loginRepository.findAll();
        Long lastLoginId = allLogin.isEmpty() ? 1 : allLogin.get(allLogin.size() - 1).getUser().getId() + 1;

        loginRepository.save(new Login(lastLoginId, login.getUser(), login.getDevice(), login.getCountry(),
                LocalDateTime.now(), false));

        boolean LoginMatch = LoginDelUser.isEmpty();
        
        if (LoginMatch) {
            return new Alert(login.getUser().getId(), "Device and geolocalization problem detected for " + login.getUser().getId());
        }
        return new Alert(login.getUser().getId(), "Something else");
    }
}
