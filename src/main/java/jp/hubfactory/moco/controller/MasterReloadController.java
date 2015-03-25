package jp.hubfactory.moco.controller;

import jp.hubfactory.moco.cache.MstGirlCache;
import jp.hubfactory.moco.cache.MstGirlMissionCache;
import jp.hubfactory.moco.cache.MstVoiceCache;
import jp.hubfactory.moco.cache.MstVoiceSetCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * マスターリロードコントローラー
 *
 */
@RestController
@RequestMapping(value="/master-reload")
public class MasterReloadController {

    private static final Logger logger = LoggerFactory.getLogger(MasterReloadController.class);

    @Autowired
    private MstGirlCache mstGirlCache;
    @Autowired
    private MstVoiceCache mstVoiceCache;
    @Autowired
    private MstVoiceSetCache mstVoiceSetCache;
    @Autowired
    private MstGirlMissionCache mstGirlMissionCache;

    @RequestMapping(value = "/fa8ed510d830fff7ec209533e38fc3e2106404f02a4b504ced14e969be0151913645e126beaf136f", method = RequestMethod.POST)
    public ResponseEntity<Boolean> index() {
        logger.info("master reload start.");
        mstGirlCache.load();
        mstVoiceCache.load();
        mstVoiceSetCache.load();
        mstGirlMissionCache.load();
        logger.info("master reload end.");
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
}
