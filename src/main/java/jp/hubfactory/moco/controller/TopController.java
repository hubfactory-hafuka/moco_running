package jp.hubfactory.moco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@EnableAutoConfiguration
public class TopController {
    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "Welcome Moco Running";
    }
}
