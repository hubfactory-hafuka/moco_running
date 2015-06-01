package jp.hubfactory.moco.controller.admin;

import java.util.Map;

import jp.hubfactory.moco.enums.PurchaseType;
import jp.hubfactory.moco.repository.UserPurchaseHistoryRepository;
import jp.hubfactory.moco.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {

    @Autowired
    private transient UserRepository userRepository;
    @Autowired
    private transient UserPurchaseHistoryRepository userPurchaseHistoryRepository;

    @RequestMapping("/adm/")
    public String index(Map<String, Object> model) {
        Long count = userRepository.count();
        model.put("count", count);
        return "index";
    }

    @RequestMapping("/adm/sales/")
    public String sales(Map<String, Object> model) {

        Integer voiceCount = userPurchaseHistoryRepository.selectCountByType(PurchaseType.VOICE.getKey());
        Integer girlCount = userPurchaseHistoryRepository.selectCountByType(PurchaseType.GIRL.getKey());

        int voiceSales = voiceCount.intValue() * 240;
        int girlSales = girlCount.intValue() * 360;
        int allSales = voiceSales + girlSales;

        model.put("allSales", allSales);
        model.put("voiceCount", voiceCount);
        model.put("voiceSales", voiceSales);
        model.put("girlSales", girlSales);
        model.put("girlCount", girlCount);

        return "sales";
    }
}
