package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.RegistroDTO;
import co.com.wallacesoft.paymenow.dto.UsuarioDTO;

public interface AuthService {
UsuarioDTO registrar(RegistroDTO dto);
}
