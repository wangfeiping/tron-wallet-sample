package tron.wallet.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import tron.wallet.util.AES;

@Slf4j
@Data
public class Wallet {
  private final String PrivateKey;
  private final String address;

  public Wallet(String privateKey, String address) {
    PrivateKey = privateKey;
    this.address = address;
  }

  public static Wallet loadWallet(String walletFile) {
    try {
      File destination = new File(walletFile);
      destination.createNewFile();
      BufferedReader br = new BufferedReader(new FileReader(destination));
      JSONObject credentialJson = JSON.parseObject(br.readLine());
      if (null == credentialJson || !credentialJson.containsKey("address")) {
        log.error("钱包文件加载失败{}", walletFile);
        return null;
      }
      Wallet wallet =
          new Wallet(
              AES.decryptAes(
                  credentialJson.getString("privateKey"),
                  AES.HashAndSalt(
                      credentialJson.getString("user").getBytes(StandardCharsets.UTF_8))),
              credentialJson.getString("address"));
      br.close();
      return wallet;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
