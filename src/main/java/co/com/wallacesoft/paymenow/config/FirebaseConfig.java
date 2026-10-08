package co.com.wallacesoft.paymenow.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Configuration 
public class FirebaseConfig {

    @Value("${firebase.credentials.path}")
    private String credentialsPath;

    @Bean 
    public FirebaseApp firebaseApp()throws IOException{
        if(!FirebaseApp.getApps().isEmpty()){
            return FirebaseApp.getInstance();
        }
        try(InputStream in = new FileInputStream(credentialsPath)){
            FirebaseOptions options =FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(in)).build();
            return FirebaseApp.initializeApp(options);
        }
    }
    @Bean 
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp){
        return FirebaseAuth.getInstance(firebaseApp);
    }

}
