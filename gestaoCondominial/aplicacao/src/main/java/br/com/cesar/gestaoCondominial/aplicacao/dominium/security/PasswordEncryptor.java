package br.com.cesar.gestaoCondominial.aplicacao.dominium.security;

public interface PasswordEncryptor {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
