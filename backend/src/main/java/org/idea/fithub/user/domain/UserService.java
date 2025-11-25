package org.idea.fithub.user.domain;

import lombok.RequiredArgsConstructor;
import org.idea.fithub.user.infrastructure.BaseUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final BaseUserRepository<User> baseUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException {
        return  baseUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + username));
    }
}