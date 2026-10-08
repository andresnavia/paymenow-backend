package co.com.wallacesoft.paymenow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import co.com.wallacesoft.paymenow.constants.Roles;
import co.com.wallacesoft.paymenow.dto.PersonaDTO;
import co.com.wallacesoft.paymenow.dto.RegistroDTO;
import co.com.wallacesoft.paymenow.dto.UsuarioDTO;
import co.com.wallacesoft.paymenow.exception.BusinessException;
import co.com.wallacesoft.paymenow.service.AuthService;
import co.com.wallacesoft.paymenow.service.PersonaService;
import co.com.wallacesoft.paymenow.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service 
@RequiredArgsConstructor 
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final PersonaService personaService;
    private final UsuarioService usuarioService;
    private final FirebaseAuth firebaseAuth;
    @Override
    public UsuarioDTO registrar(RegistroDTO dto) {
        if (dto==null) {
            throw new BusinessException(
                    "Informacion del registro vacia.");
        }
        //Consultamos la persona
        PersonaDTO personaDTO=personaService.findByIdentificacion(dto.identificacion());
        
        
        //Creamos la persona
        if(personaDTO==null ){
            personaDTO=personaService.create(new PersonaDTO(null, dto.primerNombre(), dto.segundoNombre(),
            dto.primerApellido(),dto.segundoApellido(), dto.sexo(),dto.fechaNacimiento(),
            dto.idTiid(), null, dto.identificacion(),
            dto.email(), dto.telefono(), dto.celular(), null, null));
        }else{
            if (usuarioService.existeUsuarioPersona(personaDTO.idPers(), Roles.CLIENTE)) {
                throw new BusinessException("La persona ya tiene un usuario registrado");
            }
        }

        //Creamos el usuario en firebase
        String idFirebase = null;
        try {
            //CreateRequest valida el email y el password localmente y lanza IllegalArgumentException
            UserRecord.CreateRequest requestCreateUser= new UserRecord.CreateRequest().setEmail(personaDTO.email()).setPassword(dto.password()).setDisplayName(personaDTO.primerNombre()+"-"+personaDTO.primerApellido());
            idFirebase=firebaseAuth.createUser(requestCreateUser).getUid();
        } catch (IllegalArgumentException e) {
            throw new BusinessException("El email o el password no tienen un formato valido.");
        } catch (FirebaseAuthException e) {
            log.error("Error creando el usuario en Firebase para el email={} authErrorCode={} errorCode={}",
                    personaDTO.email(), e.getAuthErrorCode(), e.getErrorCode(), e);
            throw new BusinessException(mensajeErrorFirebase(e));
        }
        try {
            //creamos el usuario en la base de datos
            UsuarioDTO usuarioCreado = usuarioService.create(new UsuarioDTO(null, personaDTO.idPers(), Roles.CLIENTE, idFirebase, null, null));
            
            return usuarioCreado;
        } catch (RuntimeException e) {
            try {
                firebaseAuth.deleteUser(idFirebase);
            } catch (FirebaseAuthException  ex) {
                log.error("Usuario huérfano en Firebase uid={}", idFirebase, ex);
            }
            throw e;
        }
    }

    private String mensajeErrorFirebase(FirebaseAuthException e) {
        //Primero los codigos especificos de autenticacion
        if (e.getAuthErrorCode() != null) {
            switch (e.getAuthErrorCode()) {
                case EMAIL_ALREADY_EXISTS:
                    return "El email ya se encuentra registrado.";
                case CONFIGURATION_NOT_FOUND:
                    return "El registro con email y password no esta habilitado.";
                default:
                    break;
            }
        }
        //Luego los codigos generales de Firebase
        if (e.getErrorCode() != null) {
            switch (e.getErrorCode()) {
                case INVALID_ARGUMENT:
                    return "Los datos del usuario no son validos. Verifique el email y el password.";
                case UNAVAILABLE:
                case DEADLINE_EXCEEDED:
                    return "El servicio de autenticacion no esta disponible. Intente mas tarde.";
                case RESOURCE_EXHAUSTED:
                    return "Se han realizado demasiados intentos. Intente mas tarde.";
                default:
                    break;
            }
        }
        return "Se presento un error en la creacion del usuario.";
    }

}
