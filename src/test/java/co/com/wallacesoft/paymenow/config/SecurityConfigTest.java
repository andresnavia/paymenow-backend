package co.com.wallacesoft.paymenow.config;

import co.com.wallacesoft.paymenow.constants.Roles;
import co.com.wallacesoft.paymenow.controller.AuthController;
import co.com.wallacesoft.paymenow.controller.CuentaAsociadaController;
import co.com.wallacesoft.paymenow.controller.CuentaController;
import co.com.wallacesoft.paymenow.controller.PersonaController;
import co.com.wallacesoft.paymenow.controller.PlataformaController;
import co.com.wallacesoft.paymenow.controller.TiposIdentificacionController;
import co.com.wallacesoft.paymenow.dto.PerfilDTO;
import co.com.wallacesoft.paymenow.repository.UsuarioRepository;
import co.com.wallacesoft.paymenow.security.UsuarioAutenticado;
import co.com.wallacesoft.paymenow.service.AuthService;
import co.com.wallacesoft.paymenow.service.CuentaAsociadaService;
import co.com.wallacesoft.paymenow.service.CuentaService;
import co.com.wallacesoft.paymenow.service.PersonaService;
import co.com.wallacesoft.paymenow.service.PlataformaService;
import co.com.wallacesoft.paymenow.service.TiposIdentificacionService;
import com.google.firebase.ErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, PersonaController.class, CuentaController.class,
        CuentaAsociadaController.class, PlataformaController.class, TiposIdentificacionController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {

    private static final String CUENTA_JSON = """
            {"idPlat":1,"idPers":1,"activo":"S","fechaPago":"2030-01-15"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean private FirebaseAuth firebaseAuth;
    @MockBean private UsuarioRepository usuarioRepository;
    @MockBean private AuthService authService;
    @MockBean private PersonaService personaService;
    @MockBean private CuentaService cuentaService;
    @MockBean private CuentaAsociadaService cuentaAsociadaService;
    @MockBean private PlataformaService plataformaService;
    @MockBean private TiposIdentificacionService tiposIdentificacionService;

    @BeforeEach
    void setUp() throws Exception {
        registrarToken("token-admin", "uid-admin", new UsuarioAutenticado(1, 1, Roles.ADMIN));
        registrarToken("token-cliente", "uid-cliente", new UsuarioAutenticado(2, 2, Roles.CLIENTE));
        registrarToken("token-rol-raro", "uid-rol-raro", new UsuarioAutenticado(3, 3, 99));

        FirebaseToken sinUsuario = mock(FirebaseToken.class);
        when(sinUsuario.getUid()).thenReturn("uid-sin-usuario");
        when(firebaseAuth.verifyIdToken("token-sin-usuario")).thenReturn(sinUsuario);
        when(usuarioRepository.findActivosByIdFirebase("uid-sin-usuario")).thenReturn(List.of());

        when(firebaseAuth.verifyIdToken("token-invalido")).thenThrow(
                new FirebaseAuthException(ErrorCode.INVALID_ARGUMENT, "invalido", null, null, null));
    }

    private void registrarToken(String token, String uid, UsuarioAutenticado usuario) throws Exception {
        FirebaseToken firebaseToken = Mockito.mock(FirebaseToken.class);
        when(firebaseToken.getUid()).thenReturn(uid);
        when(firebaseAuth.verifyIdToken(token)).thenReturn(firebaseToken);
        when(usuarioRepository.findActivosByIdFirebase(uid)).thenReturn(List.of(usuario));
    }

    private MockHttpServletRequestBuilder con(String token, MockHttpServletRequestBuilder request) {
        return request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    private MockHttpServletRequestBuilder conCuenta(MockHttpServletRequestBuilder request) {
        return request.contentType(MediaType.APPLICATION_JSON).content(CUENTA_JSON);
    }

    @Test
    void sinToken_deberiaRetornar401ConMensaje() throws Exception {
        mockMvc.perform(get("/api/v1/cuentas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Debes iniciar sesion para realizar esta accion."));
    }

    @Test
    void tokenInvalidoUsuarioInexistenteORolDesconocido_deberianRetornar401() throws Exception {
        for (String token : List.of("token-invalido", "token-sin-usuario", "token-rol-raro")) {
            mockMvc.perform(con(token, get("/api/v1/cuentas"))).andExpect(status().isUnauthorized());
        }
    }

    @Test
    void rutasPublicas_noDeberianExigirToken() throws Exception {
        mockMvc.perform(get("/api/v1/tipos-identificacion")).andExpect(status().isOk());
        mockMvc.perform(options("/api/v1/cuentas")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk());
    }

    @Test
    void perfil_deberiaResponderAlUsuarioAutenticado() throws Exception {
        when(authService.perfilActual()).thenReturn(new PerfilDTO(2, Roles.CLIENTE, "Usuario", "Ana Gomez", false));

        mockMvc.perform(con("token-cliente", get("/api/v1/auth/me")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPers").value(2))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    void cliente_deberiaConsultarCrearYModificarSusRecursos() throws Exception {
        for (String url : List.of("/api/v1/cuentas", "/api/v1/cuentas-asociadas", "/api/v1/personas",
                "/api/v1/plataformas", "/api/v1/cuentas/contar")) {
            mockMvc.perform(con("token-cliente", get(url))).andExpect(status().isOk());
        }
        mockMvc.perform(con("token-cliente", get("/api/v1/personas/buscar").param("identificacion", "123")))
                .andExpect(status().isOk());
        mockMvc.perform(conCuenta(con("token-cliente", post("/api/v1/cuentas")))).andExpect(status().isCreated());
        mockMvc.perform(conCuenta(con("token-cliente", put("/api/v1/cuentas/1")))).andExpect(status().isOk());
        mockMvc.perform(con("token-cliente", post("/api/v1/cuentas-asociadas/por-identificacion"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idCuen":1,"identificacion":"123"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void cliente_noDeberiaEliminarNiAdministrarCatalogos() throws Exception {
        List<MockHttpServletRequestBuilder> prohibidas = List.of(
                delete("/api/v1/cuentas/1"),
                delete("/api/v1/cuentas-asociadas/1"),
                post("/api/v1/cuentas-asociadas"),
                delete("/api/v1/personas/1"),
                post("/api/v1/personas"),
                put("/api/v1/personas/1"),
                post("/api/v1/plataformas"),
                get("/api/v1/usuarios"),
                get("/api/v1/roles"),
                get("/api/v1/pagos"),
                get("/api/v1/parametros"),
                get("/api/v1/estados-pago"),
                get("/api/v1/tipos-identificacion/1"));

        for (MockHttpServletRequestBuilder request : prohibidas) {
            mockMvc.perform(con("token-cliente", request))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("No tienes permisos para realizar esta accion."));
        }
    }

    @Test
    void admin_deberiaPoderEliminar() throws Exception {
        mockMvc.perform(con("token-admin", delete("/api/v1/cuentas/1"))).andExpect(status().isNoContent());
    }
}
