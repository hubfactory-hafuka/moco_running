package jp.hubfactory.moco.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Test {

    public static void main(String[] args) {

        try {
            File file = new File("C:\\tmp\\prism.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));
            File outfile = new File("C:\\tmp\\prism_user.csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));


            String str = br.readLine();

            Map<Long, Integer> userPrice = new LinkedHashMap<Long, Integer>();

            while (str != null) {

                String[] lines = str.split(",");
                Integer gachaId = Integer.parseInt(lines[0]);
                Long userId = Long.valueOf(lines[1]);
                Integer count = Integer.parseInt(lines[2]);

                int price = 0;

                if (gachaId == 8065) {
                    price = price + (500 * count);
                } else if (gachaId == 8066) {
                    price = price + (1000 * count);
                } else if (gachaId == 8067) {
                    price = price + (2000 * count);
                } else if (gachaId == 8068) {
                    price = price + (3000 * count);
                } else if (gachaId == 8069) {
                    price = price + (3000 * count);
                }

                int uPrice = userPrice.get(userId) != null ? userPrice.get(userId) : 0;

                userPrice.put(userId, uPrice + price);

                str = br.readLine();
            }

            br.close();


            for (Entry<Long, Integer> entry : userPrice.entrySet()) {
                Long userId = entry.getKey();
                Integer price = entry.getValue();
                bw.append(userId + "," + price);
                bw.newLine();

            }

            bw.close();





        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }

        System.out.println("終了");






    }
}
