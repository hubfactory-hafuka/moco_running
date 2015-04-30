package jp.hubfactory.moco.purchase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * アプリ内課金レシート検証クラス
 *
 */
@Component
public class VerifyReceipt {

    public int verifyReceipt(String receipt, String itunesPath) throws Exception {
        int status = -1;

        // 本番用
        URL url = new URL(itunesPath);

        // make connection, use post mode
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setAllowUserInteraction(false);

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
        }
        br.close();
        String response = sb.toString();

        // Deserialize response
        JsonNode result = mapper.readTree(response);

        status = result.get("status").asInt();
        return status;

    }
}
