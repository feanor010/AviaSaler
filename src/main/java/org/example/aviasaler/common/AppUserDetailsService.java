package org.example.aviasaler.common;

import org.example.aviasaler.auth.model.AuthIdentity;
import org.example.aviasaler.auth.model.AuthProvider;
import org.example.aviasaler.auth.model.CustomUserDetails;
import org.example.aviasaler.auth.repository.AuthIdentityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AuthIdentityRepository authIdentityRepository;

    public AppUserDetailsService(AuthIdentityRepository authIdentityRepository) {
        this.authIdentityRepository = authIdentityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthIdentity authIdentity = authIdentityRepository.findByProviderAndProviderUserId(AuthProvider.LOCAL, username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return new CustomUserDetails(authIdentity.getUser(), authIdentity.getPasswordHash());
    }
}
