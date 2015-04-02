package jp.hubfactory.moco.purchase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import jp.hubfactory.moco.MocoProperties;
import jp.hubfactory.moco.bean.PurchaseResponseBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * アプリ内課金レシート検証クラス
 *
 */
@Component
public class VerifyReceipt {

    private static final Logger logger = LoggerFactory
            .getLogger(VerifyReceipt.class);

    @Autowired
    private MocoProperties mocoProperties;

    public boolean verifyReceipt(String receipt) throws Exception {
        int status = -1;

        String path = mocoProperties.getSystem().getItunes();
        // This is the URL of the REST webservice in iTunes App Store
        URL url = new URL(path);

        // make connection, use post mode
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setAllowUserInteraction(false);

        // Encode the binary receipt data into Base 64
        // Here I'm using org.apache.commons.codec.binary.Base64 as an encoder,
        // since commons-codec is already in Grails classpath
        // Base64 encoder = new Base64();
        // //encoder.decode(pArray);
        // String encodedReceipt = new String(encoder.encode(receipt));

        // Create a JSON query object
        // Here I'm using Grails' org.codehaus.groovy.grails.web.json.JSONObject
        Map<String, String> map = new HashMap<>();
        map.put("receipt-data", receipt);

        ObjectMapper mapper = new ObjectMapper();
        String jsonObject = mapper.writeValueAsString(map);

        // Write the JSON query object to the connection output stream
        PrintStream ps = new PrintStream(connection.getOutputStream());
        ps.print(jsonObject);
        ps.close();

        // Call the service
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        // Extract response
        String str;
        StringBuffer sb = new StringBuffer();
        while ((str = br.readLine()) != null) {
            sb.append(str);
            sb.append("/n");
        }
        br.close();
        String response = sb.toString();

        // Deserialize response
        ObjectMapper mapper2 = new ObjectMapper();
        JsonNode result = mapper.readTree(response);
        PurchaseResponseBean bean = mapper2.convertValue(result, PurchaseResponseBean.class);

        status = bean.getStatus();
        if (status == 0) {
            // provide content
            return true;
        } else {
            // signal error, throw an exception, do your stuff honey!
            logger.error("レシート認証エラー. status=" + status);
            return false;
        }

        // return status ;

    }
}
