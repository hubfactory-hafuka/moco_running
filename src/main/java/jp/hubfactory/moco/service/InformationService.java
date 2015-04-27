package jp.hubfactory.moco.service;

import jp.hubfactory.moco.bean.InformationBean;
import jp.hubfactory.moco.cache.MstInformationCache;
import jp.hubfactory.moco.entity.MstInformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InformationService {

    @Autowired
    private MstInformationCache mstInformationCache;

    /**
     * お知らせ情報取得
     * @return
     */
    public InformationBean getInformation() {
        InformationBean infoBean = null;
        MstInformation mstInformation = mstInformationCache.getActiveInformation();
        if (mstInformation != null) {
            infoBean = new InformationBean();
            infoBean.setTitle(mstInformation.getTitle());
            infoBean.setText(mstInformation.getText());
        }
        return infoBean;
    }
}
