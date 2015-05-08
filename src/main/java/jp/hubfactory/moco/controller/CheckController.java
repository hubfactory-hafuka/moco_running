package jp.hubfactory.moco.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/check")
public class CheckController {

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseStatus(value=HttpStatus.OK)
    public Boolean check() {
        return true;
    }
}
