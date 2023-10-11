package zone.yby.lab.utils;

import org.springframework.security.crypto.password.PasswordEncoder;

public class SelfPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return HashCode.encode(rawPassword.toString(), "#lab20220426$2y$04$H5Xpw6m8Uo3ol4XuUkAFk.5hECgsjZ0sW/U9C2DU8aoS7D3b98H8G");
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
