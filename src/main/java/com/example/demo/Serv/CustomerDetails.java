package com.example.demo.Serv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Model.pojo;
import com.example.demo.Repo.ConnectivityDB;

@Service
public class CustomerDetails  implements UserDetailsService {


    @Autowired
    private ConnectivityDB d;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Username is " + username);
        if (username == null) {
            throw new UsernameNotFoundException("Username is null");
        }
        pojo cu = d.findByUsername(username);
        if (cu == null) {
            throw new UsernameNotFoundException("User not found for username: " + username);
        }
       
       
        return User.withUsername(cu.getUsername())
                   .password(cu.getPassword())
                   .roles("User")
                   .build();
    }
    


}
