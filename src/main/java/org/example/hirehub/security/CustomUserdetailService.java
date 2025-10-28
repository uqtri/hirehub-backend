package org.example.hirehub.security;

import org.example.hirehub.repository.UserRepository;
import org.example.hirehub.service.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserdetailService implements UserDetailsService {

    private final UserRepository userRepository;
    public CustomUserdetailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return new CustomUserDetails(this.userRepository.findByEmail(username));
    }

}
