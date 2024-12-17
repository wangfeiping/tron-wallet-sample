package tron.wallet.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import tron.wallet.entity.ApiResponse;
import tron.wallet.entity.MessageResult;
import tron.wallet.service.TronService;
import tron.wallet.component.TronWatcher;

@Slf4j
@RestController
@RequestMapping("/tron")
public class TronController {
  
  @Autowired private TronWatcher watcher;
  @Autowired private TronService tronService;

  @GetMapping("height")
  public MessageResult getHeight() {
    try {
      long rpcBlockNumber = tronService.getHeight();
      MessageResult result = new MessageResult(0, "success");
      result.setData(rpcBlockNumber);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      return MessageResult.error(500, "查询失败,error:" + e.getMessage());
    }
  }

  @GetMapping("address/{account}")
  public MessageResult getNewAddress(@PathVariable String account) {
    log.info("create new account={}", account);
    try {
      String address = tronService.createNewWallet(account, "a12345678!");
      MessageResult result = new MessageResult(0, "success");
      result.setData(address);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      return MessageResult.error(500, "rpc error:" + e.getMessage());
    }
  }
}
