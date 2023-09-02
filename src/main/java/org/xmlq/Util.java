package org.xmlq;

import com.microsoft.playwright.Page;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Consumer;

public class Util {

    /**
     * <li>执行function</li>
     * <li>执行function打印耗时</li>
     * <li>全屏截屏快照</li>
     */
    public static void executeFunc(Consumer<?> consumer, Page page, String screenShotFileName, String taskName) {
        long statTime = System.currentTimeMillis();
        consumer.accept(null);
        long endTime = System.currentTimeMillis();
        printInfo(taskName, endTime - statTime);
        // 是否需要截屏
        if (Objects.nonNull(screenShotFileName)) {
            screenShot(page, screenShotFileName);
        }
    }

    private static void printInfo(String eventMsg, long time) {
        System.out.printf("%s,耗时:%s毫秒%n", eventMsg, time);
    }

    /**
     * 截屏
     */
    private static void screenShot(Page page, String fileName) {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("pic/" + fileName))
                .setFullPage(true));
    }
}
