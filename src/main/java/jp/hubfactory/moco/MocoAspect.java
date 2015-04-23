package jp.hubfactory.moco;

import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.form.BaseForm;
import jp.hubfactory.moco.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MocoAspect {

    @Autowired
    private UserService userService;

    @Before("execution(* jp.hubfactory.moco.controller..*.*(..))")
    public void before(JoinPoint jp) {
        String signatureStr = jp.getSignature().toString();
        // ログイン時、マスターリロード時はチェックしない
        if (signatureStr.indexOf("login") >= 0 || signatureStr.indexOf("masterReload") >= 0) {
            return;
        }
        BaseForm form = (BaseForm)jp.getArgs()[0];
        if (StringUtils.isEmpty(form.getToken())) {
            throw new IllegalStateException("token is invalid.");
        }
        User user = userService.getUser(form.getUserId());
        if (user == null) {
            throw new IllegalStateException("user is null.");
        }
        if (!StringUtils.equals(form.getToken(), user.getToken())) {
            throw new IllegalStateException("token is invalid.");
        }

//        Object[] objArray = jp.getArgs();
//        for (Object obj : objArray) {
//            BaseForm form = (GetGirlsForm)obj;
//            System.out.println("TOKEN=" + form.getToken());
//        }
    }
}
