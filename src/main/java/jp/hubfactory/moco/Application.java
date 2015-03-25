package jp.hubfactory.moco;

import jp.hubfactory.moco.cache.MstGirlCache;
import jp.hubfactory.moco.cache.MstGirlMissionCache;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.cache.MstVoiceSetCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// public class Application extends SpringBootServletInitializer {
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    // @Override
    // protected SpringApplicationBuilder configure(SpringApplicationBuilder
    // application) {
    // return super.configure(application);
    // }

    @Autowired
    private MstGirlCache mstGirlCache;
    @Autowired
    private MstVoiceCache mstVoiceCache;
    @Autowired
    private MstVoiceSetCache mstVoiceSetCache;
    @Autowired
    private MstGirlMissionCache mstGirlMissionCache;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        logger.info("master load start.");
        mstGirlCache.load();
        mstVoiceCache.load();
        mstVoiceSetCache.load();
        mstGirlMissionCache.load();
        logger.info("master load end.");
    }

}