package co.com.wallacesoft.paymenow.exception;

/**
 * Excepcion para reglas de negocio violadas (ej. duplicados, limites, etc.)
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
