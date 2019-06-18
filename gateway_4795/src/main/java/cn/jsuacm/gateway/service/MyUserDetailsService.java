package cn.jsuacm.gateway.service;

import cn.jsuacm.gateway.pojo.User;
import cn.jsuacm.gateway.util.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {
    
    
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        User user = userService.getUser(s);
        String pwd = "";
        if (user != null){
            pwd = user.getPassword();
        }else {
            throw new UsernameNotFoundException(String.format("No user found with username."));
        }
        List<String> grantedAuthoritiesStr = authenticationService.getGrantedAuthorities(user.getUid());
        for (String grantedAuthority:grantedAuthoritiesStr){
            GrantedAuthority au = new SimpleGrantedAuthority(grantedAuthority);
            grantedAuthorities.add(au);
        }
        JwtUser jwtUser = new JwtUser(s, pwd, grantedAuthorities);


        return jwtUser;
    }
}
