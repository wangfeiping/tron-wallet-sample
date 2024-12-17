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
import tron.wallet.util.Wallet;

@Slf4j
@Component
// @Service
public class TronService {

  @Autowired private Coin coin;
  @Autowired private TronApi tronApi;

  public String getWithdrawAddress() {
    Wallet wallet = Wallet.loadWallet(coin.getKeystorePath() + coin.getWithdrawWallet());
    return TronUtils.getAddressByPrivateKey(wallet.getPrivateKey());
  }
  
  public long getHeight() throws IOException{
    return tronApi.getHeight();
  }

}

