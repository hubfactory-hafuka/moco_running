package jp.hubfactory.moco;

import jp.hubfactory.moco.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// public class Application extends SpringBootServletInitializer {
public class Application implements CommandLineRunner {
    // @Override
    // protected SpringApplicationBuilder configure(SpringApplicationBuilder
    // application) {
    // return super.configure(application);
    // }

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        userRepository.findAll();

        System.out.println("run");

    }

}