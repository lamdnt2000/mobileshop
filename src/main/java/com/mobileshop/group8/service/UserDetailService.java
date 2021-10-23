package com.mobileshop.group8.service;

import com.mobileshop.group8.model.Member;
import com.mobileshop.group8.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
            Member user = userRepository.findByUserId(id);
            if (user == null){
                throw new UsernameNotFoundException("Could not found user");
            }
            return new MyUserDetails(user);
    }
}
