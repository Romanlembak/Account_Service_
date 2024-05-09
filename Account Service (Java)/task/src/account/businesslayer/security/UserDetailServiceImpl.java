package account.businesslayer.security;

import account.businesslayer.entity.Account;
import account.businesslayer.security.AccountAdapter;
import account.persistence.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    AccountRepository repository;
    public UserDetailServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = repository.findByEmail(username.toLowerCase()).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        return new AccountAdapter(account);
    }
}
