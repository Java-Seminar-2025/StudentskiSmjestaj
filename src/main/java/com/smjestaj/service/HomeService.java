package com.smjestaj.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class HomeService {
    public String getLoggedInUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
