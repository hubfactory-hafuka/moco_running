package jp.hubfactory.moco;

import java.sql.DriverManager;
import java.util.Collections;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

@Configuration
//@EnableWebMvc
//@ComponentScan
//public class WebApplicationConfig extends WebMvcConfigurerAdapter {
public class WebApplicationConfig {

//    /**
//     * インターセプター
//     * @param registry
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new AccessInterceptor()).addPathPatterns("/**");
//    }

    @Bean
    protected ServletContextListener listener() {
        return new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                // TODO 自動生成されたメソッド・スタブ

            }
            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                System.out.println("########### ServletContextListener#contextDestroyed ############");
                /*
                 * Tomcat再起動時に [Abandoned connection cleanup thread] がメモリリークしてるという警告が出る場合の対応
                 */
                try {
                    AbandonedConnectionCleanupThread.shutdown();
                } catch (final InterruptedException e) {
                }
                /*
                 * SEVERE: A web application registered the JBDC driver [com.mysql.jdbc.Driver] but failed to unregister it when the web application was stopped. To prevent a memory leak, the JDBC Driver has been forcibly unregistered.
                 * 対応
                 */
                Collections.list(DriverManager.getDrivers()).forEach(driver -> {
                    try {
                        DriverManager.deregisterDriver(driver);
                    } catch (final Exception e) {}
                });
            }
        };
    }

}
