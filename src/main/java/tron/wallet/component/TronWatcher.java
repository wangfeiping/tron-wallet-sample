package tron.wallet.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.sdk.TronApi;
import org.tron.utils.TronUtils;
import org.web3j.utils.Convert;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import tron.wallet.entity.Coin;
import tron.wallet.entity.Deposit;
import tron.wallet.service.TronService;

@Slf4j
@Component
public class TronWatcher extends Watcher {

  @Autowired private TronService tronService;

  @Override
  public Long getBlockHeight() {
    try {
      long height = tronService.getHeight();
      // long height = 100000L;
      log.info("current block height: {}", height);
      return height;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return 0L;
    }
  }

  @Override
  public void checkBalance(){
    String withdrawAddress = tronService.getWithdrawAddress();
    log.info("checked withdraw account balance: {}; {}", withdrawAddress, TronUtils.toHexAddress(withdrawAddress));
  }

  @Override
  public void txConfirmed(Deposit deposit){

  }

  @EventListener(ApplicationReadyEvent.class)
  @Override
  public void run(){
    super.run();
  }
  
  @Override
  public List<Deposit> replayBlock(Long start, Long end) {
    List<Deposit> deposits = new ArrayList<>();

    log.info("block replayed: TronApi.tronUrl={}; start={}; end={}", TronApi.tronUrl, start, end);
    return deposits;
  }
  
}
