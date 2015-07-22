package jp.hubfactory.moco;

import jp.hubfactory.moco.cache.MstGirlCache;
import jp.hubfactory.moco.cache.MstGirlMissionCache;
import jp.hubfactory.moco.cache.MstInformationCache;
import jp.hubfactory.moco.cache.MstRankingCache;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.cache.MstVoiceSetCache;
import jp.hubfactory.moco.cache.MstVoiceSetDetailCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
//@EnableCaching
 public class Application extends SpringBootServletInitializer implements CommandLineRunner {

//    private static final Logger logger = LoggerFactory.getLogger(Application.class);

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
    private MstVoiceSetDetailCache mstVoiceSetDetailCache;
    @Autowired
    private MstGirlMissionCache mstGirlMissionCache;
    @Autowired
    private MstInformationCache mstInformationCache;
    @Autowired
    private MstRankingCache mstRankingCache;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 起動時処理
     */
    @Override
    public void run(String... args) throws Exception {
        logger.info("master load start.");
        mstGirlCache.load();
        mstVoiceCache.load();
        mstVoiceSetCache.load();
        mstVoiceSetDetailCache.load();
        mstGirlMissionCache.load();
        mstInformationCache.load();
        mstRankingCache.load();
        logger.info("master load end.");
    }

//    @Bean
//    public CacheManager cacheManager() {
//        return new ConcurrentMapCacheManager("mstVoiceCache");
//    }
}