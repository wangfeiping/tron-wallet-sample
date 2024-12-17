package tron.wallet.component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.crypto.Wallet;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import tron.wallet.entity.Coin;
import tron.wallet.entity.WatcherSetting;
import tron.wallet.entity.Deposit;
import tron.wallet.service.TronService;

@Slf4j
@Data
public abstract class Watcher implements Runnable {
  private boolean stop = false;
  private Long currentBlockHeight = 0L;
  
  @Autowired private WatcherSetting setting;
  @Autowired private Coin coin;

  public Watcher() {
  }

  /**
   * 获取最新区块
   * @return
   */
  public abstract Long getBlockHeight();

  /**
   * 重放区块，检查提币账户收款（入账）交易；
   * @param start
   * @param end
   * @return
   */
  public abstract List<Deposit> replayBlock(Long start, Long end);

  /**
   * 查询提币账户余额
   */
  public abstract void checkBalance();

  /**
   * 收款（入账）交易确认成功通知
   * @param deposit
   */
  public abstract void txConfirmed(Deposit deposit);

  /**
   * 定时检查区块：
   *     1）检查收款交易；
   *     2）检查提币账户余额；
   */
  @Override
  public void run() {
    Long initHeight = setting.getInitBlockHeight();
    Long interval = setting.getInterval();
    int confirmation = setting.getConfirmation();
    int step = setting.getStep();
    log.info("init block height: {}", initHeight);
    log.info("interval: {}", interval);
    log.info("confirmation: {}", confirmation);
    log.info("step: {}", step);

    currentBlockHeight = initHeight > loadLastSyncHeight() ?  initHeight: loadLastSyncHeight();

    stop = false;
    long nextCheck = 0;
    while (!(Thread.interrupted() || stop)) {
      if (nextCheck <= System.currentTimeMillis()) {
        try {
          nextCheck = System.currentTimeMillis() + interval;
          check();
          checkBalance();
        } catch (Exception ex) {
          log.error(ex.getMessage(), ex);
        }
      } else {
        try {
          Thread.sleep(interval);
        } catch (InterruptedException ex) {
          log.error(ex.getMessage(), ex);
        }
      }
    }
  }

  public void check() {
    try {
      Long height = getBlockHeight();
      // 如果初始为0，从最新区块开始处理
      if (currentBlockHeight <= 0){
        currentBlockHeight = height - setting.getConfirmation();
      }
      height = height - setting.getConfirmation() + 1;
      if (currentBlockHeight < height) {
        // 每次最多重放处理 step 个区块
        long start = currentBlockHeight + 1;
        currentBlockHeight = (height - currentBlockHeight > setting.getStep())
                ? currentBlockHeight + setting.getStep()
                : height;
        // 重放检查区块，获取收款（入账）交易
        List<Deposit> deposits = replayBlock(start, currentBlockHeight);
        log.info("block replayed from {} to {}", start, currentBlockHeight);
        deposits.forEach(
            deposit -> {
              txConfirmed(deposit);
            });
        updateLastSyncHeight(currentBlockHeight);
      } else {
        log.info("already latest height {}, nothing to do!", currentBlockHeight);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 缓存以同步检查的区块高度，以便重启时不会重复检查。
   */
  public void updateLastSyncHeight(Long height){
  }
  
  public Long loadLastSyncHeight(){
    return 0L;
  }
}
