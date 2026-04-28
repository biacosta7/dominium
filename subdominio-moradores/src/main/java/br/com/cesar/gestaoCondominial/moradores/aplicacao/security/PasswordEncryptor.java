package br.com.cesar.gestaoCondominial.moradores.aplicacao.security;

public interface PasswordEncryptor {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
