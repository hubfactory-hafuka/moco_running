package jp.hubfactory.moco.controller;

import java.util.Map;

import jp.hubfactory.moco.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {

    @Autowired
    private transient UserRepository userRepository;

    @RequestMapping("/adm/")
    public String welcome(Map<String, Object> model) {
        Long count = userRepository.count();
        model.put("count", count);
        return "index";
    }
}
