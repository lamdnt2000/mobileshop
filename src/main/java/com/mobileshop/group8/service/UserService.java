package com.mobileshop.group8.service;

import com.mobileshop.group8.model.Member;
import com.mobileshop.group8.model.Role;
import com.mobileshop.group8.repository.RoleRepository;
import com.mobileshop.group8.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public void save(Member user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByRoleId(2);
        user.setRoleByRoleId(role);
        userRepository.save(user);
    }

    public Member checkLogin(String userId, String password){
        return userRepository.findByUserIdAndPassword(userId,password);
    }
}
