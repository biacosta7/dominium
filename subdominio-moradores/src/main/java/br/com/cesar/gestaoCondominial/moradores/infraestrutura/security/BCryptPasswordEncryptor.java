package br.com.cesar.gestaoCondominial.moradores.infraestrutura.security;

import br.com.cesar.gestaoCondominial.moradores.aplicacao.security.PasswordEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncryptor implements PasswordEncryptor {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordEncryptor(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
