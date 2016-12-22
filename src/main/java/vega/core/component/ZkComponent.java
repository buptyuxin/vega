package vega.core.component;

import org.apache.commons.lang3.StringUtils;
import vega.common.CallBack;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import vega.config.ConfigUtil;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by yanmo.yx on 2016/8/3.
 */
public class ZkComponent implements Component{

    private CuratorFramework zkClient;

    private static volatile ZkComponent instance;

    private ZkComponent() {
        zkClient = CuratorFrameworkFactory.newClient(ConfigUtil.getZkServers(), new ExponentialBackoffRetry(1000, 3));
        zkClient.start();
    }

    // singleton model
    // dcl
    public static ZkComponent singleton() {
        if (null == instance) {
            synchronized (ZkComponent.class) {
                if (null == instance) {
                    instance = new ZkComponent();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
    }

    @Override
    public void shutdown() {
        zkClient.close();
    }

//    public void watch(String path, CallBack callBack) {
//        try {
//            if (zkClient.checkExists().forPath(path) == null) {
//                // 节点不存在
//                zkClient.create().creatingParentsIfNeeded().forPath(path);
//            }
//            zkClient.getChildren().usingWatcher((Watcher) message -> callBack.doCallBack(message)).inBackground().forPath(path);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void watchChild(String path, CallBack callBack) {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
        try {
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pathChildrenCache.getListenable().addListener((client, event) -> {
            switch (event.getType()) {
                case CHILD_ADDED:
                    callBack.doCallBack(PathChildrenCacheEvent.Type.CHILD_ADDED, event);
                    break;
                case CHILD_UPDATED:
                    callBack.doCallBack(PathChildrenCacheEvent.Type.CHILD_UPDATED, event);
                    break;
                case CHILD_REMOVED:
                    callBack.doCallBack(PathChildrenCacheEvent.Type.CHILD_REMOVED, event);
                    break;
                default:
                    break;
            }
        });

    }

    public static void main(String[] args) {
//        1.
//        CuratorFramework zkClient = CuratorFrameworkFactory.newClient("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183", new ExponentialBackoffRetry(1000, 3));
//        zkClient.start();
//
//        zkClient.close();

//        String s = "afas";
//        Optional<String> optional = Optional.ofNullable(s);
//        optional.filter(Objects::nonNull).filter(value -> StringUtils.isNotBlank(value) == true).ifPresent(value -> {
//            System.out.println(value);
//            return;
//        });

//        2.
//        BigDecimal customDutyRate = new BigDecimal("15");
//        BigDecimal exciseDutyRate = new BigDecimal("10");
//        BigDecimal vatRate = new BigDecimal("5");
//        BigDecimal discount = new BigDecimal("20");
//
//        BigDecimal cc = customDutyRate.divide(new BigDecimal("100"));
//        BigDecimal ee = exciseDutyRate.divide(new BigDecimal("100"));
//        BigDecimal vv = vatRate.divide(new BigDecimal("100"));
//
//        BigDecimal comprehensiveTaxRate = cc.add(vv).add(cc.multiply(vv))
//                .add(ee.add(ee.multiply(cc)).add(ee.multiply(vv)).add(ee.multiply(cc).multiply(vv))
//                        .divide(new BigDecimal("1").subtract(ee), 3))
//                .multiply(discount);
//
//        double c = customDutyRate.doubleValue()/100;
//        double e = exciseDutyRate.doubleValue()/100;
//        double v = vatRate.doubleValue()/100;
//        int d = (int)discount.doubleValue();
//
//        System.out.println(comprehensiveTaxRate.setScale(3, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
//        System.out.println((long) ((c + v + c * v + (e + e * c + e * v + e * c * v) / (1 - e)) * 1000 * d)/1000.00);

//        3.
//        BigDecimal tf = BigDecimal.valueOf(100);
//
//        BigDecimal dd = BigDecimal.valueOf(20).divide(new BigDecimal("100"));
//        BigDecimal cc = BigDecimal.valueOf(15).divide(new BigDecimal("100"));
//        BigDecimal ee = BigDecimal.valueOf(10).divide(new BigDecimal("100"));
//        BigDecimal vv = BigDecimal.valueOf(5).divide(new BigDecimal("100"));
//
//        // 倒算完税价金额
//        long ctf = tf.divide(cc.add(vv).add(cc.multiply(vv)).add(ee.add(ee.multiply(cc)).add(ee.multiply(vv)).add(ee.multiply(vv).multiply(cc)).divide(BigDecimal.ONE.subtract(ee), 3, BigDecimal.ROUND_HALF_UP)).multiply(dd).add(BigDecimal.ONE), 0, BigDecimal.ROUND_FLOOR).longValue();
//        System.out.println(ctf);
//
//        long ttf = 100;
//        double d = 20 / 100.00;
//        double c = 15 / 100.00;
//        double e = 10 / 100.00;
//        double v = 5 / 100.00;
//
//        long cctf = (long) (ttf / ((c + v + c * v + (e + e * c + e * v + e * v * c) / (1 - e)) * d + 1));
//        System.out.println(cctf);

//        4.
        BigDecimal bd = new BigDecimal("10.4");
        BigDecimal bd1 = BigDecimal.valueOf(4);
        System.out.println(bd.divide(bd1, 0 , BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
    }
}
