package com.example.crm.Dto;

import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.UsersRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthenticationResponse {
    private String accessToken;
    private String message;

    @Service
    public static class MyUserDetailsService implements UserDetailsService {

        /**
         * Locates the user based on the username. In the actual implementation, the search
         * may possibly be case sensitive, or case insensitive depending on how the
         * implementation instance is configured. In this case, the <code>UserDetails</code>
         * object that comes back may have a username that is of a different case than what
         * was actually requested..
         *
         * @param username the username identifying the user whose data is required.
         * @return a fully populated user record (never <code>null</code>)
         * @throws UsernameNotFoundException if the user could not be found or the user has no
         *                                   GrantedAuthority
         */
        @Autowired
        private UsersRepo repo;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            UsersModel user = repo.findByEmail(email);
            if(user==null){
                throw new UsernameNotFoundException("User not found");
            }
            return new UserPrincipal(user);
        }
    }
}
