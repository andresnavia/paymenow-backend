package co.com.wallacesoft.paymenow.controller;

import co.com.wallacesoft.paymenow.constants.Roles;
import co.com.wallacesoft.paymenow.dto.RegistroDTO;
import co.com.wallacesoft.paymenow.dto.UsuarioDTO;
import co.com.wallacesoft.paymenow.exception.BusinessException;
import co.com.wallacesoft.paymenow.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    private static final String URL_REGISTRO = "/api/v1/auth/register";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private RegistroDTO registroValido() {
        return new RegistroDTO("Ana", null, "Gomez", null, "F", LocalDate.of(1995, 5, 10),
                1, null, "123456", "ana@correo.com", null, "3001234567", "secreto123");
    }

    @Test
    void registrar_conDatosValidos_deberiaRetornar201ConUsuarioCreado() throws Exception {
        UsuarioDTO usuarioCreado = new UsuarioDTO(1, 10, Roles.CLIENTE, "uid-firebase-1", "S", LocalDate.now());
        when(authService.registrar(any(RegistroDTO.class))).thenReturn(usuarioCreado);

        mockMvc.perform(post(URL_REGISTRO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsua").value(1))
                .andExpect(jsonPath("$.idPers").value(10))
                .andExpect(jsonPath("$.idRol").value(Roles.CLIENTE))
                .andExpect(jsonPath("$.idFirebase").value("uid-firebase-1"));

        verify(authService).registrar(any(RegistroDTO.class));
    }

    @Test
    void registrar_sinCamposObligatorios_deberiaRetornar400SinLlamarAlServicio() throws Exception {
        RegistroDTO dto = new RegistroDTO(null, null, null, null, null, null,
                null, null, null, null, null, null, null);

        mockMvc.perform(post(URL_REGISTRO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error de validacion en los datos enviados"));

        verify(authService, never()).registrar(any());
    }

    @Test
    void registrar_conPasswordCorto_deberiaRetornar400() throws Exception {
        RegistroDTO base = registroValido();
        RegistroDTO dto = new RegistroDTO(base.primerNombre(), null, base.primerApellido(), null, base.sexo(),
                base.fechaNacimiento(), base.idTiid(), null, base.identificacion(), base.email(),
                null, base.celular(), "123");

        mockMvc.perform(post(URL_REGISTRO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registrar(any());
    }

    @Test
    void registrar_conEmailInvalido_deberiaRetornar400() throws Exception {
        RegistroDTO base = registroValido();
        RegistroDTO dto = new RegistroDTO(base.primerNombre(), null, base.primerApellido(), null, base.sexo(),
                base.fechaNacimiento(), base.idTiid(), null, base.identificacion(), "correo-invalido",
                null, base.celular(), base.password());

        mockMvc.perform(post(URL_REGISTRO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registrar(any());
    }

    @Test
    void registrar_cuandoLaPersonaYaTieneUsuario_deberiaRetornar409() throws Exception {
        when(authService.registrar(any(RegistroDTO.class)))
                .thenThrow(new BusinessException("La persona ya tiene un usuario registrado"));

        mockMvc.perform(post(URL_REGISTRO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroValido())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La persona ya tiene un usuario registrado"));
    }
}
