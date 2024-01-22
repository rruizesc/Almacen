package main.java.org.example;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Articulo {
    private String nombre;
    private int cantidad;

    // Constructores, getters y setters

    @Override
    public String toString() {
        return "Articulo [nombre=" + nombre + ", cantidad=" + cantidad + "]";
    }
}
