package com.spring.demo.configuration;

import com.spring.demo.entity.User;
import com.spring.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional=userRepository.findByEmail(email);
        if(!userOptional.isPresent()){
            throw new UsernameNotFoundException("Invalid email "+email);
        }
        else {
            return new CustomerDetailService(userOptional.get());
        }


    }
}
