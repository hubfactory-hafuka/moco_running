//package jp.hubfactory.moco.intercepter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import jp.hubfactory.moco.service.UserService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//public class AccessInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    private UserService userService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
////        String body = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
////        System.out.println("####BODY###=" + body);
//        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request,
//            HttpServletResponse response, Object handler,
//            ModelAndView modelAndView) throws Exception {
//        // TODO 自動生成されたメソッド・スタブ
//        System.out.println("postHandle");
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request,
//            HttpServletResponse response, Object handler, Exception ex)
//            throws Exception {
//        // TODO 自動生成されたメソッド・スタブ
//        System.out.println("afterCompletion");
//    }
//
//}
