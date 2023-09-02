package org.xmlq;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class App {
    private static final String NEED_LOGIN = "login";

    public static void main(String[] args) throws Exception {
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
        launchOptions.setHeadless(false);
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(launchOptions);
        try {
            // 首次登陆
            if (args.length > 0 && NEED_LOGIN.equalsIgnoreCase(args[0])) {
                BrowserContext loginContext = browser.newContext();
                Page loginPage = loginContext.newPage();
                firstLongin(loginPage, loginContext);
                System.out.println("10S后关闭当前已登录状态浏览器...");
                // 等待20S后自动关闭 以便观察
                loginPage.waitForTimeout(10 * 1000);
                loginPage.close();
                loginContext.close();

                Thread.sleep(2000L);
            }

            System.out.println("-----------------------\n-----------------------\n-----------------------");

            System.out.println("准备执行免密码登陆测试...");

            // 使用已有帐号 免密码登陆
            BrowserContext browserContext = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(Paths.get("state.json")));
            Page page = browserContext.newPage();
            doCheckWithCookie(page);
            page.close();
            browserContext.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            browser.close();
            playwright.close();
        }

    }

    public static void doCheckWithCookie(Page page) {
        openIndexWithCookie(page);
        enterKdzq(page);
    }

    /**
     * 免密码登陆首页
     */
    public static void openIndexWithCookie(Page page) {
        Util.executeFunc((i) -> {
            page.navigate("https://www.10086.cn/index/nm/index_471_470.html");
            assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("[退出]"))).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(30 * 1000));
        }, page, null, "免登陆加载首页完成,当前位置:[首页]");
    }

    /**
     * 进入宽带专区
     */
    public static void enterKdzq(Page page) {
        Util.executeFunc((i) -> {
            // 进入首页轮播图1：足不出户办宽带，方便又快捷
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("足不出户办宽带，方便又快捷")).click();
            page.waitForLoadState(LoadState.LOAD);
        }, page, "kdzq.png", "宽带专区页面加载完成,当前位置:[首页]>>>[轮播图-足不出户办宽带]>>>[宽带专区]");

        // 停留5S 用以观察页面
        page.waitForTimeout(5 * 1000);

        // 进入98元套餐办理页面
        Util.executeFunc((i) -> {
            // 进入首页轮播图1：足不出户办宽带，方便又快捷
            page.getByText("98元保底享宽带300M 98元/月 98元保底享宽带300M").click();
            page.waitForLoadState(LoadState.LOAD);
        }, page, "kdzq98tc.png", "98元保底享宽带套餐页面加载完成,当前位置:[首页]>>>[轮播图-足不出户办宽带]>>>[宽带专区]>>>[98元宽带套餐办理]");


        // 停留5S 用以观察页面
        page.waitForTimeout(5 * 1000);
    }

    /**
     * 首次登陆：需要借助人工登陆。登陆后保存登陆信息供后续使用
     */
    public static void firstLongin(Page page, BrowserContext browserContext) {
        // 加载主页
        openIndex(page);
        // 执行登陆
        login10086(page, browserContext);

    }

    public static void openIndex(Page page) {
        Util.executeFunc((i) -> {
            page.navigate("https://www.10086.cn/index/nm/index_471_470.html");
            page.waitForLoadState(LoadState.LOAD);
        }, page, "indexLoad.png", "加载首页完成,当前位置:[首页](未登录)");
    }

    public static void login10086(Page page, BrowserContext context) {
        Util.executeFunc((i) -> {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("请登录")).click();//
            assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("[退出]"))).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(300 * 1000));
            context.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get("state.json")));
        }, page, "firstLogin.png", "登陆完成,已成功记录登陆信息,后续可免登陆...");

    }
}
