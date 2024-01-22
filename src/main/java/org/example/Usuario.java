package main.java.org.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    private String username;
    private String password;

    // Constructores, getters y setters

    @Override
    public String toString() {
        return "Usuario [username=" + username + ", password=" + password + "]";
    }
}
