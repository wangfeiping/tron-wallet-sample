package tron.wallet.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
// import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import org.tron.sdk.TronApi;
import org.tron.utils.TronUtils;
import org.web3j.utils.Convert;

import tron.wallet.entity.Coin;
import tron.wallet.entity.Contract;
import tron.wallet.util.AES;
import tron.wallet.util.Wallet;

@Slf4j
@Component
// @Service
public class TronService {

  @Autowired private Coin coin;
  @Autowired private Contract contract;
  @Autowired private TronApi tronApi;
  
  public String getWithdrawAddress() {
    Wallet wallet = Wallet.loadWallet(coin.getKeystorePath() + coin.getWithdrawWallet());
    return TronUtils.getAddressByPrivateKey(wallet.getPrivateKey());
  }
  
  private static String getWalletFileName(String address) {
    DateTimeFormatter format = DateTimeFormatter.ofPattern("'UTC--'yyyy-MM-dd'T'HH-mm-ss.nVV'--'");
    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    return now.format(format) + address + ".json";
  }

  public long getHeight() throws IOException{
    return tronApi.getHeight();
  }

  public String createNewWallet(String account, String password) throws Exception {
    // "address", "hexAddress", "privateKey"
    Map<String, String> r = TronApi.createAddress();
    String hashSalt = AES.HashAndSalt(password.getBytes(StandardCharsets.UTF_8));
    String encPriKey = AES.encryptAes(r.get("privateKey"), hashSalt);
    r.put("privateKey", encPriKey);
    r.put("user", password);
    String address = r.get("address");
    String fileName = getWalletFileName(address);

    String keyStorePath = coin.getKeystorePath();
    File destination = new File(keyStorePath, fileName);
    destination.createNewFile();
    FileWriter output = new FileWriter(destination);
    output.write(JSON.toJSONString(r));
    output.close();

    return address;
  }

  public BigDecimal getBalance(String address) throws IOException {
    String balanceStr = tronApi.getAccountBalance(address);
    JSONObject balance = JSON.parseObject(balanceStr);
    if (null != balance && balance.containsKey("balance")) {
      return new BigDecimal(balance.getLong("balance"));
    }
    return new BigDecimal(-1);
  }

  public BigDecimal getBalanceOf(String address) throws IOException {
    // String tAddr = TronUtils.toHexAddress(contract.getAddress());
    // String tAddr = TronUtils.toViewAddress(contract.getAddress());
    // String balanceStr = tronApi.getBalanceOf(address, tAddr);
    String balanceStr = tronApi.getBalanceOf(address, contract.getAddress());
    JSONObject balance = JSON.parseObject(balanceStr);
    if (null != balance && balance.containsKey("constant_result")) {
      String balanceHex = balance.getJSONArray("constant_result").getString(0);
      return new BigDecimal(new BigInteger(balanceHex, 16));
    }
    return new BigDecimal(-1);
  }
}

