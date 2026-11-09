package com.cesde.barberia.config;

import com.cesde.barberia.dto.UsuarioRegistroDTO;
import com.cesde.barberia.modelo.Barbero;
import com.cesde.barberia.modelo.Cliente;
import com.cesde.barberia.modelo.Servicio;
import com.cesde.barberia.repositorio.IBarbero;
import com.cesde.barberia.repositorio.ICliente;
import com.cesde.barberia.repositorio.IServicio;
import com.cesde.barberia.repositorio.IUsuario;
import com.cesde.barberia.servicio.SUsuario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Carga datos de ejemplo la primera vez que arranca la aplicacion,
// solo en las tablas que esten vacias.
@Component
public class DatosIniciales implements CommandLineRunner {

    private final IUsuario iUsuario;
    private final SUsuario sUsuario;
    private final IServicio iServicio;
    private final IBarbero iBarbero;
    private final ICliente iCliente;

    public DatosIniciales(IUsuario iUsuario, SUsuario sUsuario, IServicio iServicio,
                          IBarbero iBarbero, ICliente iCliente) {
        this.iUsuario = iUsuario;
        this.sUsuario = sUsuario;
        this.iServicio = iServicio;
        this.iBarbero = iBarbero;
        this.iCliente = iCliente;
    }

    @Override
    public void run(String... args) {
        try {
            if (iUsuario.count() == 0) {
                // Cuenta de demostracion: cambiar la contrasena despues del primer ingreso
                sUsuario.adicionar(new UsuarioRegistroDTO(
                        "Administrador", "admin@barberia.com", "Barberia2026*", "Administrador", true));
            }
            if (iServicio.count() == 0) {
                iServicio.save(servicio("Corte clasico", 25000, 30));
                iServicio.save(servicio("Corte y barba", 38000, 45));
                iServicio.save(servicio("Arreglo de barba", 18000, 20));
                iServicio.save(servicio("Degradado", 30000, 40));
                iServicio.save(servicio("Tinte", 60000, 90));
            }
            if (iBarbero.count() == 0) {
                iBarbero.save(barbero("71345678", "Andres Zapata", "3004567890", "Degradado"));
                iBarbero.save(barbero("1037654321", "Julian Restrepo", "3112345678", "Barba"));
                iBarbero.save(barbero("43987654", "Camila Ospina", "3209876543", "Colorimetria"));
            }
            if (iCliente.count() == 0) {
                iCliente.save(cliente("1017123456", "Santiago Gomez", "3015551234", "santiago@correo.com"));
                iCliente.save(cliente("1152987654", "Mateo Alvarez", "3145559876", "mateo@correo.com"));
            }
        } catch (Exception e) {
            System.out.println("No fue posible cargar los datos iniciales: " + e.getMessage());
        }
    }

    private Servicio servicio(String nombre, int precio, int duracion) {
        Servicio servicio = new Servicio();
        servicio.setNomservicio(nombre);
        servicio.setPrecio(precio);
        servicio.setDuracion(duracion);
        servicio.setActivo(true);
        return servicio;
    }

    private Barbero barbero(String id, String nombre, String telefono, String especialidad) {
        Barbero barbero = new Barbero();
        barbero.setIdbarbero(id);
        barbero.setNombarbero(nombre);
        barbero.setTelbarbero(telefono);
        barbero.setEspecialidad(especialidad);
        barbero.setActivo(true);
        return barbero;
    }

    private Cliente cliente(String id, String nombre, String telefono, String correo) {
        Cliente cliente = new Cliente();
        cliente.setIdcliente(id);
        cliente.setNomcliente(nombre);
        cliente.setTelcliente(telefono);
        cliente.setCorreo(correo);
        cliente.setActivo(true);
        return cliente;
    }
}
