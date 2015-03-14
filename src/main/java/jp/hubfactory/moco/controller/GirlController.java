package jp.hubfactory.moco.controller;

import java.util.List;

import jp.hubfactory.moco.entity.MstGirl;
import jp.hubfactory.moco.form.InputForm;
import jp.hubfactory.moco.service.MstGirlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api/girl")
public class GirlController {

    private static final Logger logger = LoggerFactory.getLogger(GirlController.class);

    @Autowired
    private MstGirlService mstGirlService;

//    @RequestMapping(value="/add", method = RequestMethod.POST)
//    @ResponseStatus(HttpStatus.CREATED)
//    public MstGirl insertMstGirl(@Validated @RequestBody MstGirl mstGirl) {
//        return mstGirlService.insert(mstGirl);
//    }

    @RequestMapping(value = "/get-all", method = RequestMethod.POST)
    public ResponseEntity<List<MstGirl>> getAll() {
        logger.info("GirlController#getAll");
        List<MstGirl> mstGirlList = mstGirlService.selectMstGirls();
        return new ResponseEntity<List<MstGirl>>(mstGirlList, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-one", method = RequestMethod.POST)
    public ResponseEntity<MstGirl> getOne(@RequestBody InputForm inputForm) {
        logger.info("GirlController#getOne");
        MstGirl mstGirl = new MstGirl();
        if (inputForm.getGirlId() == null) {
            return new ResponseEntity<MstGirl>(mstGirl, HttpStatus.BAD_REQUEST);
        }
        mstGirl = mstGirlService.selectMstGirl(inputForm.getGirlId());
        return new ResponseEntity<MstGirl>(mstGirl, HttpStatus.OK);
    }
}
