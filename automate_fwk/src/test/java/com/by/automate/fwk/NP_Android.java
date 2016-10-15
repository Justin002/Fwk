package com.by.automate.fwk;

import java.lang.Thread.State;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.by.automate.base.AndroidApp;

@SuppressWarnings("deprecation")
public class NP_Android extends AndroidApp {

    protected String closeButtonForAndroid = "<id>=hk.com.nextmedia.magazine.nextmediaplus.qa:id/splashad_close";

    public NP_Android() {

        super();
    }

    public NP_Android(String core) {
        super(core);
    }

    protected String getAppName() {

        return "NextPlus_HK";
    }

    /**
     * click skip button going to home page.
     */
    public void goHomePage() {

        verifyIsShown("skipButton");
        tapOn("skipButton");
        log("Going to home page.");

        verifyIsShown("slideButton");
        verifyIsShown("NPPlusLog");
        verifyIsShown("playButton");
        verifyIsShown("bestAttention");
        verifyIsShown("bestHit");

        String bestAttentionText = getValueOf("bestAttention");
        String bestHitText = getValueOf("bestHit");

        String V_bestAttentionText = getContentPropertry("np.msg.home.bestAttection");
        String V_bestHitText = getContentPropertry("np.msg.home.bestHit");

        assertEquest(bestAttentionText, V_bestAttentionText);
        assertEquest(bestHitText, V_bestHitText);

        tapOn("bestAttention");

        verifyIsShown("headline_text");
        if (getValueOf("headline_text").equals("載入中")) {
            for (int i = 0; i < 3; i++) {
                waitByTimeout(4000);
            }
            if (getValueOf("headline_text").equals("載入中")) {
                assertTrue(false, "The home page loaded failed.");
            }
        }
        verifyIsShown("headline_image");

        if (waitForElementReadyOnTimeOut("headline_viewCount", 5)) {
            verifyIsShown("headline_viewCount");
            log(getValueOf("headline_viewCount"));
        }

        verifyIsShown("switcherView");

        swipe(5, 8, 5, 4);
        waitByTimeOut(1000);
        verifyIsShown("articlePictureItem", 1);
        // verifyIsShown("articleSortItem", 1);
        verifyIsShown("articleTitleItem", 1);

        verifyIsShown("firstArticlePictrue");
        verifyIsShown("firstArticleTitle");
        // verifyIsShown("firstArticleSort");

        String title = getValueOf("articleTitleItem", 1);
        String V_title = getValueOf("firstArticleTitle");

        assertEquest(title, V_title);
    }

    /**
     * 比较新闻列表中的title和详细新闻的title.
     * 
     * @param title
     * @param titleDetails
     */
    public void verifyTitle(String title, String titleDetails) {

        String s = title.substring(title.length() - 1, title.length());
        if (StringUtils.equals(s, "…")) {
            title = StringUtils.replace(title, "…", "");
            assertContains(titleDetails, title);
        } else {
            assertEquest(title, titleDetails);
        }
    }

    public String verifyArticleDetails(String storyTitle) {
        String articleTitle = "";
        verifyIsShown("back");
        verifyIsShown("pageTitleText");
        //
        String pageTitle = getValueOf("pageTitleText");
        String V_pageTitle = getContentPropertry("np.msg.home.pageTitle");
        verifyIsShown("textSize");
        // 判斷是否是video
        if (waitForElementReadyOnTimeOut("videoModule", 8)) {
            verifyIsShown("articleTitle");
        } else {
            swipeToText(storyTitle);
        }

        verifyIsShown("articlePublishData");
        verifyIsShown("articleViewCount");
        verifyIsShown("articleAddCommentPicture");
        verifyIsShown("articleCommentCount");
        verifyIsShown("articleFBLike");

        log("Check article title.");
        articleTitle = getValueOf("articleTitle");
        verifyTitle(storyTitle, articleTitle);
        assertEquest(pageTitle, V_pageTitle);
        return articleTitle;
    }

    public void changeTextSieze() {

        waitByTimeOut(3000);
        clickOn("textSize");
        verifyIsShown("decreaseButton");
        verifyIsShown("increaseButton");
        verifyIsShown("fontsSizeText");
        String changeFontsSizeText = getValueOf("fontsSizeText");
        String V_changeFontsSizeText = getContentPropertry("np.detailsPage.FontsSizeText");
        assertEquest(changeFontsSizeText, V_changeFontsSizeText);

        // 縮小
        clickOn("decreaseButton");
        verifyIsShown("decreaseButton");
        verifyIsShown("increaseButton");
        verifyIsShown("fontsSizeText");
        changeFontsSizeText = getValueOf("fontsSizeText");
        V_changeFontsSizeText = getContentPropertry("np.detailsPage.FontsSizeText");
        assertEquest(changeFontsSizeText, V_changeFontsSizeText);
        // 增長
        clickOn("increaseButton");
        verifyIsShown("decreaseButton");
        verifyIsShown("increaseButton");
        verifyIsShown("fontsSizeText");
        changeFontsSizeText = getValueOf("fontsSizeText");
        V_changeFontsSizeText = getContentPropertry("np.detailsPage.FontsSizeText");
        assertEquest(changeFontsSizeText, V_changeFontsSizeText);
        clickOn("textSize");

    }

    /**
     * play video
     */

    public void clickVideoCentent() {

        int y1 = getElementY("articleTitle");
        int y2 = getElementY("videoModule");
        int y3 = (y1 - y2) / 2 + y2;

        int x = getCoorinateX();
        tapOnByXY(x * 0.5, y3);
        waitByTimeOut(100);
        tapOnByXY(x * 0.5, y3);
    }

    // stop
    public void suspendOnDetail() {

        log("Suspend");
        int y1 = getElementY("articleTitle");
        int y2 = getElementY("videoModule");
        int y3 = (y1 - y2) / 2 + y2;

        int x = getCoorinateX();
        tapOnByXY((x * 0.5), y3);
        waitByTimeOut(100);
        tapOnByXY((x * 0.5), y3);

        if (waitForElementReadyOnTimeOut("videoCurrentTime", 5)) {
            verifyIsShown("videoCurrentTime");
            verifyIsShown("videoTotalTime");
            String currentTime = getValueOf("videoCurrentTime");
            String totalTime = getValueOf("videoTotalTime");
            log("Current_Time:" + currentTime + " --- tatal_time:" + totalTime);
        }
        waitByTimeOut(1000);
    }

    // 快進
    public void speedOnDetail() {

        log("Speed.");
        verifyIsShown("videolinear");
        int y1 = getElementY("articleTitle");
        int y2 = getElementY("videolinear");
        int y = (y1 - y2) / 2;
        tapOnByXY((getCoorinateX() * 0.4), y1 - y);

        clickVideoCentent();
        waitByTimeOut(5000);
        suspendOnDetail();

        if (waitForElementReadyOnTimeOut("videoCurrentTime", 5)) {
            verifyIsShown("videoCurrentTime");
            verifyIsShown("videoTotalTime");
            String currentTime = getValueOf("videoCurrentTime");
            String totalTime = getValueOf("videoTotalTime");
            log("Current_Time:" + currentTime + " --- tatal_time:" + totalTime);
        }
        waitByTimeOut(1000);
        waitByTimeOut(1000);
    }

    // 快退
    public void fastReverseOnDetail() {

        log("Fast Reverse.");
        verifyIsShown("videolinear");
        int y1 = getElementY("articleTitle");
        int y2 = getElementY("videolinear");
        int y = (y1 - y2) / 2;
        tapOnByXY((getCoorinateX() * 0.2), y1 - y);
        waitByTimeOut(300);
        clickVideoCentent();
        waitByTimeOut(5000);
        suspendOnDetail();

        if (waitForElementReadyOnTimeOut("videoCurrentTime", 5)) {
            verifyIsShown("videoCurrentTime");
            verifyIsShown("videoTotalTime");
            String currentTime = getValueOf("videoCurrentTime");
            String totalTime = getValueOf("videoTotalTime");
            log("Current_Time:" + currentTime + " --- tatal_time:" + totalTime);
        }
        waitByTimeOut(1000);
        waitByTimeOut(1000);
    }

    // full shot playing
    public void maximize() {

        log("Maimize");
        verifyIsShown("videolinear");
        verifyIsShown("videoExtendButton");
        clickOn("videoExtendButton");
        /*
         * int y1 = getElementY("article_Title"); int y2 =
         * getElementY("videolinear"); int y = (y1 - y2) / 2;
         * 
         * tapOn((int) (getCoorinateX() * 0.9), y1 - y);
         */
        /*
         * verifyIsShown("videolinear"); int y1 = getElementY("article_Title");
         * int y2 = getElementY("videolinear"); int y = (y1 - y2) / 2;
         * 
         * tapOnByXY((getCoorinateX() * 0.9), y1 - y);
         */

    }

    public void checkVideoOnAritcleDatails(String articleTitle) {
        for (int i = 0; i < 8; i++) {
            clickVideoCentent();
            if (!waitForElementReadyOnTimeOut("videoPlayButton", 5)) {
                log("Loading....");
                waitByTimeOut(8000);
            } else {
                int y1 = getElementY("articleTitle");
                int y2 = getElementY("videoModule");
                int y3 = (y1 - y2) / 2 + y2;
                int x = getCoorinateX();
                tapOnByXY((x * 0.5), y3);
                break;
            }
        }

        verifyIsShown("videoModule");
        // stop.
        suspendOnDetail();
        waitByTimeOut(1000);

        // speed
        speedOnDetail();
        waitByTimeOut(1000);

        // reverse
        fastReverseOnDetail();
        waitByTimeOut(1000);

        // maximize
        maximize();

        /*
         * // verify play video on click maximize button after
         * updatedUiMap("homePage:play"); playVideo(title);
         * 
         * updatedUiMap("homePage:articleInfo");
         */

    }

    public void playVideoOnFullPage(String articleTitle) {

        double x = getCoorinateX() * 0.5;
        double y = getCoorinateY() * 0.5;
        waitByTimeOut(500);
        verifyIsShown("fullVideeLoding");
        verifyIsNotShown("fullVideeLoding");
        // 驗證 是否有廣告
        if (waitForElementReadyOnTimeOut("fullKonwDetails", 15)) {
            verifyIsShown("fullSkipGuangGao");
            tapOn("fullSkipGuangGao");
            waitByTimeOut(15000);
        }

        waitByTimeOut(2000);
        tapOnByXY(x, y);
        waitByTimeOut(500);
        tapOnByXY(x, y);
        verifyIsShown("fullClosePlay");
        verifyIsShown("fullShareButton");
        verifyIsShown("fullMoreVideo");
        verifyIsShown("fullProgress");
        verifyIsShown("fullNewsTitle");
        String currentTitle = getValueOf("fullNewsTitle");
        verifyTitle(articleTitle, currentTitle);
        // speed 快進
        speed();
        tapOnByXY(x, y);
        waitByTimeOut(8000);
        tapOnByXY(x, y);
        waitByTimeOut(500);
        tapOnByXY(x, y);
        waitByTimeOut(500);
        tapOnByXY(x, y);
        verifyIsShown("fullClosePlay");
        // 快退
        fastReverse();
        /*
         * verifyIsShown("fullClosePlay"); tapOn("fullClosePlay");
         */

    }

    public void speed() {
        double x = getCoorinateX() * 0.5;
        double y = getCoorinateY() * 0.5;
        String total = getValueOf("fullTotalTime");
        String currentTime = getValueOf("fullCurrentTime");

        int ELX = getCoorinateX();
        int ELY = getElementY("fullProgress");
        tapOnByXY((int) (ELX * 0.4), ELY + 50);
        waitByTimeOut(2000);
        tapOnByXY(x, y);
        waitByTimeOut(500);
        tapOnByXY(x, y);
        waitByTimeOut(2000);
        String afterTotal = getValueOf("fullTotalTime");
        String afterCurrentTime = getValueOf("fullCurrentTime");

        assertEquest(total, afterTotal);
        log("currentTime : " + currentTime + " || " + "afterCurrentTime:" + afterCurrentTime);
    }

    public void fastReverse() {

        double x = getCoorinateX() * 0.5;
        double y = getCoorinateY() * 0.5;
        String total = getValueOf("fullTotalTime");
        String currentTime = getValueOf("fullCurrentTime");

        int ELX = getCoorinateX();
        int ELY = getElementY("fullProgress");
        tapOnByXY((int) (ELX * 0.3), ELY + 50);
        waitByTimeOut(2000);
        tapOnByXY(x, y);
        waitByTimeOut(500);
        tapOnByXY(x, y);
        String afterTotal = getValueOf("fullTotalTime");
        String afterCurrentTime = getValueOf("fullCurrentTime");
        assertEquest(total, afterTotal);
        log("currentTime : " + currentTime + " || " + "afterCurrentTime:" + afterCurrentTime);

    }

    public void shareVideoOnFullPlayingPage() {

        verifyIsShown("fullShareButton");
        clickOn("fullShareButton");

    }

    // FB like
    public void clickFBLike() {

        verifyIsShown("articleTitle");
        verifyIsShown("articleSocialBar");
        waitByTimeOut(2000);
        for (int i = 0; i < 3; i++) {
            if (!waitForElementReadyOnTimeOut("articleFBLike", 2)) {
                swipeByXY((int) (getCoorinateX() * 0.5), (int) (getCoorinateY() * 0.6), (int) (getCoorinateX() * 0.5),
                        (int) (getCoorinateY() * 0.9), 1000);
                waitByTimeOut(500);
            } else {
                break;
            }
        }
        slideToElement("articlePraise");
        verifyIsShown("articlePraise");
        verifyIsShown("articleFBLike");
        verifyIsShown("articleFBlikeCount");

        int before = Integer.parseInt(getElementValueForAttribute("articleFBlikeCount", "name"));
        verifyIsShown("articleFBLike");
        clickOn("articleFBLike");
        waitByTimeOut(3000);

        // 等待登錄頁面加載成功
        for (int i = 0; i < 3; i++) {
            if (!waitForElementReadyOnTimeOut("fblogo", 5)) {
                swipe("down");
            } else {
                break;
            }
            waitByTimeOut(2000);

        }
        verifyIsShown("fblogo");
        waitByTimeOut(500);
        verifyIsShown("fbEmail");
        verifyIsShown("fbSigInButton");

        setValue("fbEmail", getSutFullFileName("application.fb.email"));
        back();
        waitByTimeOut(200);
        tapOn("fbPassword");
        setValue("fbPassword", getSutFullFileName("application.fb.password"));
        back();
        waitByTimeOut(500);
        verifyIsShown("fbSigInButton");
        tapOn("fbSigInButton");
        verifyIsShown("textSize");
        slideToElement("articleTitle");
        slideToElement("articleSocialBar");

        waitByTimeOut(500);
        for (int i = 0; i < 3; i++) {
            if (!waitForElementReadyOnTimeOut("articlePraise", 5)) {
                swipeByXY((int) (getCoorinateX() * 0.5), (int) (getCoorinateY() * 0.6), (int) (getCoorinateX() * 0.5),
                        (int) (getCoorinateY() * 0.9), 1000);
                waitByTimeOut(500);
            }
        }
        verifyIsShown("articlePraise");
        verifyIsShown("articleFBlikeCount");
        verifyIsShown("articleFBLike");
        waitByTimeOut(500);

        clickOn("articleAfterLoginFBLike");
        waitByTimeOut(2000);
        verifyIsShown("articleSocialBar");
        verifyIsShown("articlePraise");
        slideToElement("articleAddCommentPicture");
        verifyIsShown("artilceAfterFBlikeCount");
        // verifyDisplayOfMessage("countPraise",
        // "When the FB like was clicked and comfirmed, the count was not increased as expected.");
        // log("before : " + before + " - after : " +
        // Integer.parseInt(getElementValueForAttribute("countPraise",
        // "name")));
        log("before : " + before + " - after : "
                + Integer.parseInt(getElementValueForAttribute("artilceAfterFBlikeCount", "name")));
        waitByTimeOut(2000);
        assertTrue(Math.abs(before - Integer.parseInt(getElementValueForAttribute("querenCount", "name"))) == 1,
                "When the FB like was clicked and comfirmed, the count was not increased as expected.");

    }

    public void checkPublishedDateAndScanNumber() {

        waitByTimeOut(2000);
        verifyIsShown("back");
        verifyIsShown("textSize");
        verifyIsShown("articleTitle");
        verifyIsShown("articleAddCommentPicture");
        verifyIsShown("articlePublishData");
        verifyIsShown("articleViewCount");
        String publishDate = getValueOf("articlePublishData");
        String scanNumber = getValueOf("articleViewCount");
        log("The article Publish date are : " + publishDate);
        log("The article View count are : " + scanNumber);
        // assertEquest(viewCount, scanNumber);

    }

    // 收藏
    public void collectionForArticle(String storyTitle) {

        slideToElement("articleCollectionButton");
        verifyIsShown("articleCollectionButton");
        String beforeCollect = getValueOf("articleCollectionText");
        tapOn("articleCollectionText");
        waitByTimeOut(4000);
        String afterCollect = getValueOf("articleCollectionText");

        // tapOn("CollectButton");
        log("Check the article has been collection.");
        waitByTimeOut(2000);
        assertEquest(afterCollect, getContentPropertry("np.collect.collected"));
        assertEquest(beforeCollect, getContentPropertry("np.collect.collection"));

    }

    // go to collect page verify it status.
    public void verifyArticleCollected(String articleTitle) {

        tapOn("back");
        verifyIsShown("slideButton");
        verifyIsShown("NPPlusLog");
        verifyIsShown("playButton");
        verifyIsShown("bestAttention");
        verifyIsShown("bestHit");

        clickOn("slideButton");
        waitByTimeOut(500);

        verifyIsShown("homePage");
        verifyIsShown("searchNews");
        verifyIsShown("celebrity");
        verifyIsShown("special");
        verifyIsShown("specialColumn");
        slideToElement("collect");
        clickOn("collect");
        // go to collect page.
        verifyIsShown("back");
        verifyIsShown("collectTitleText");
        verifyIsShown("collectAllArticleTitle", articleTitle);
        verifyIsDisplayed("collectAllArticleTitle", articleTitle, "collectAllAticlePublisdDate");
        verifyIsDisplayed("collectAllArticleTitle", articleTitle, "collectAllAticlePublisdDate");

        String publisedDate = getValueOnAndroid("collectAllArticleTitle", articleTitle, "collectAllAticlePublisdDate");

        clickOn("collectAllArticleTitle", articleTitle);

        verifyIsShown("back");
        verifyIsShown("collectDetailsPageTitleText");
        verifyIsShown("articleTitle");
        verifyIsShown("articlePublishData");
        String V_publisedDate = getValueOf("articlePublishData");

        slideToElement("articleCollectionButton");
        verifyIsShown("articleCollectionButton");
        String beforeCollect = getValueOf("articleCollectionText");
        tapOn("articleCollectionText");
        waitByTimeOut(4000);
        String afterCollect = getValueOf("articleCollectionText");

        // tapOn("CollectButton");
        log("Check the article has been collection.");
        waitByTimeOut(2000);
        assertEquest(beforeCollect, getContentPropertry("np.collect.collected"));
        assertEquest(afterCollect, getContentPropertry("np.collect.collection"));
        assertEquest(publisedDate, V_publisedDate);
        waitByTimeOut(1000);
    }

    // 避免 share 按鈕和 home 按鈕重疊 點擊時 回到主頁
    public void voerlappingElements(String firstFloor) {

        int firstX = getElement(firstFloor).getLocation().getX();
        int firstY = getElement(firstFloor).getLocation().getY();

        if (getCoorinateY() - firstY < 300) {

            swipeByXY(firstX, (getCoorinateY() * 0.9), firstX, (getCoorinateY() * 0.9) - 150, 3000);
        }

    }

    public void shareNews(String shareContent) {

        waitByTimeOut(500);
        verifyIsShown("shareToText");
        waitByTimeOut(500);
        slideToElement("facebook");
        clickOn("facebook");
        verifyIsShown("fbShareToFBText");
        verifyIsShown("fbPublishButton");
        verifyIsShown("fbContent");
        if (!waitForElementReadyOnTimeOut("fbAttachmentPhoto", 10)) {
            log("The preview a picture are loaded failed.", 3);
        }
        if (!waitForElementReadyOnTimeOut("fbAttachmentTitle", 10)) {
            log("The preview article title are loaded failed.", 3);
        }
        waitForElementReadyOnTimeOut("fbAttachmentTitle", 10);
        // share to oneself
        setValue("fbContent", shareContent);
        verifyIsShown("fbPublishButton");
        tapOn("fbPublishButton");

    }

    /**
     * 更多新闻
     */
    public void moreNews() {
        boolean status = false;
        slideToElement("articleCollectionButton");
        swipeByXY(getCoorinateX() * 0.5, getElementY("articleCollectionButton"), getCoorinateX() * 0.5,
                (getCoorinateY() * 0.5) - 100, 2000);
        String storyTitleOnMoreNewsModule = "";
        String pageTitle = "";
        if (waitForElementReadyOnTimeOut("articleMoreNewNews", 5)) {
            status = true;
            pageTitle = getValueOf("articleMoreNewNews");
            verifyIsShown("articleMoreNewNewsItem", 1);
            storyTitleOnMoreNewsModule = getValueOf("articleMoreNewNewsItem", 1);
            tapOn("articleMoreNewNewsItem", 1);

        } else if (waitForElementReadyOnTimeOut("articleRelevanceNews", 5)) {
            status = true;
            verifyIsShown("articleRelevanceNews");
            verifyIsShown("articleAllReleanceNewslink", 1);
            pageTitle = getValueOf("articleRelevanceNews");
            storyTitleOnMoreNewsModule = getValueOf("articleAllReleanceNewslink", 1);
            tapOn("articleAllReleanceNewslink", 1);
        }

        if (status) {
            log(storyTitleOnMoreNewsModule);

            verifyIsShown("back");
            verifyIsShown("moreNewsTitleText");
            waitByTimeOut(3000);

            slideToElement("articleTitle");
            verifyIsShown("articleTitle");
            String V_storyTitleOnMoreNewsModule = getValueOf("verifyIsShown");
            String V_pageTitle = getValueOf("moreNewsTitleText");
            verifyTitle(storyTitleOnMoreNewsModule, V_storyTitleOnMoreNewsModule);
            assertEquest(pageTitle, V_pageTitle);

            clickOn("back");
            verifyIsShown("back");
            verifyIsShown("pageTitleText");
        } else {
            log("The '更多最新新聞' module is not are displayed.");
        }

    }

    /**
     * 编辑媒介
     */
    public void editCommented() {
        String pageTitle = "";
        waitByTimeOut(4000);
        slideToElement("articleEditRecommendation");
        if (waitForElementReadyOnTimeOut("articleEditRecommendation", 5)) {
            verifyIsShown("articleEditRecommendationItem", 1);
            pageTitle = getValueOf("articleEditRecommendation");
            String storyTitleOnAllRecommend = getValueOf("articleEditRecommendationItem", 1);
            log(storyTitleOnAllRecommend);
            tapOn("articleEditRecommendationItem", 1);
            verifyIsShown("back");
            verifyIsShown("articleTitle");
            String V_pageTitle = getValueOf("editRecommendationText");
            String V_storyTitleOnAllRecommend = getValueOf("articleTitle");
            assertEquest(pageTitle, V_pageTitle);
            verifyTitle(storyTitleOnAllRecommend, V_storyTitleOnAllRecommend);
            clickOn("back");
            verifyIsShown("back");
            verifyIsShown("pageTitleText");

        } else {
            log("The '編輯推介' module is not are displayed.");
        }

    }

    // 跳轉 FB 驗證收藏
    public void switchToFBAndCheckNews() {

        switchToApp(getSutFullFileName("application.fb.package"), getSutFullFileName("application.fb.activity"));
        String FBActivity = getCurrentActivity();
        log(FBActivity);
        log("switch to FB successfu");

        uiMapUpdateView("FBPage:dashBoard");
        verifyIsShown("primaryButton");

        verifyIsShown("newsFeed");
        verifyIsShown("bookMarks");
        tapOn("bookMarks");
        int px = getElementX("newsFeed");
        int py = getElementY("newsFeed");
        waitByTimeOut(5000);
        tapOnByXY(px + 100, py + 150);
        waitByTimeOut(3000);
        swipe("top");
        slideToElement("shareOnFB", 1);
        verifyIsShown("shareOnFB");
        int beginY = getElementY("shareOnFB");
        swipeByXY((int) (getCoorinateX() * 0.5), beginY, (int) (getCoorinateX() * 0.5), (int) (getCoorinateY() * 0.95), 2000);
        waitByTimeOut(1000);

    }

    public void switchToNPApp() {

        log("switch to Nextplus App");
        switchToApp(getSutFullFileName("app.package"), getSutFullFileName("app.activity"));
        String npActivity = getCurrentActivity();
        assertEquest(getSutFullFileName("app.activity"), npActivity);
        uiMapUpdateView("homePage:homeView");
    }

    public void slideToElement(String elementName) {
        slideToElement("", "", elementName);
    }

    public void slideToElement(String listName, Object itemMatch) {
        slideToElement(listName, itemMatch, "");
    }

    public void slideToElement(String listName, Object itemMatch, String elementName) {
        log("Slided to element name '" + (elementName.equals("") ? listName : elementName) + "' are displayed.");
        boolean status = waitForElementReadyOnTimeOut(listName, itemMatch, elementName, 4);

        if (!status) {
            // swipe to top.
            // swipe("top");
            for (int i = 0; i < 5; i++) {
                waitByTimeOut(1000);
                swipe("up");
                waitByTimeOut(1000);

                for (int j = 0; j < 3; j++) {
                    status = waitForElementReadyOnTimeOut(listName, itemMatch, elementName, 1);
                    if (status) {
                        break;
                    }
                }
                log("Status " + status);
                if (status) {
                    break;
                }
            }
        }
        waitByTimeOut(2000);
        if (waitForElementReadyOnTimeOut(listName, itemMatch, elementName, 5)) {
            if (getCoorinateY() - getElementY(listName, itemMatch, elementName) < 200) {
                double beginX = (getCoorinateX() * 0.5);
                double befinY = (getCoorinateY() * 0.5);
                double endX = (getCoorinateX() * 0.5);
                double endY = (getCoorinateY() * 0.3);
                swipeByXY(beginX, befinY, endX, endY, 3000);
                waitByTimeOut(500);
            }
        }

    }

    protected WebElement findElementByLocator(String elementLocator) {
        String elementLocatorStr = getLocatorStr(elementLocator);
        String elementLocatorType = getLocatorType(elementLocator);
        return driver.findElement(getByObjectByType(elementLocatorStr, elementLocatorType));
    }

    protected boolean isDisplayed(String elementLocator) {
        try {
            WebElement element = findElementByLocator(elementLocator);
            boolean f = element.isDisplayed();
            if (f) {
                log("Discovers advertisement close it now.");
                element.click();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    class MonitoringAds extends Thread {

        public void run() {
            log("Start thread to monitoring app ads...");
            try {

                while (true) {

                    isDisplayed(closeButtonForAndroid);

                }
            } catch (WebDriverException e) {
                log("主線程與子線程發生衝突,這是可以接受的,忽略該錯誤.", 2);
            }
        }
    }
    MonitoringAds ads = null;
    public void monitorAds() {

        ads = new MonitoringAds();
        ads.setDaemon(true);
        ads.start();
        
    }
    
    public void stop(){
        log("停止廣告監控線程,等待視頻驗證...");
        ads.stop();
    }
    
    
    public void resume(){
        
        log("視頻驗證完成,重新喚醒線程...");
        ads.resume();
        
        State state = ads.getState();
        log("State : " + state );
    }

    
    

}
