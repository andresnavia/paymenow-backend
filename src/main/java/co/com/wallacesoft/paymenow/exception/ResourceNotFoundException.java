package co.com.wallacesoft.paymenow.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entidad, Object id) {
        super("%s no encontrado(a) con id: %s".formatted(entidad, id));
    }

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
