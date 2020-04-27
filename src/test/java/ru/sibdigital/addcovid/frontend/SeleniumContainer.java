package ru.sibdigital.addcovid.frontend;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.sibdigital.addcovid.frontend.pages.OrganizationAddPage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;

@Log4j2
@Data
@NoArgsConstructor
public class SeleniumContainer{

    private static SeleniumContainer instance;
    private String baseUrl;

    private WebDriver driver;
    private BrowserMobProxy proxy;
    private OrganizationAddPage organizationAddPage;




    public static SeleniumContainer getInstance(final boolean showBrowser,final int port, final String protocol, final String host, String chromeDriverLocation , CertificateUtil certificateUtil){
        String baseUrl = String.format("%s://%s:%d", protocol, host, port);

        if(instance == null){
            instance = new SeleniumContainer(showBrowser,port, protocol, host, chromeDriverLocation, certificateUtil);
        } else {
            if(!baseUrl.equals(instance.getBaseUrl())){
                instance.closeDriverAndProxy();
                instance = new SeleniumContainer(showBrowser,port, protocol, host, chromeDriverLocation, certificateUtil);
            }
        }
        return instance;
    }

    public void closeDriverAndProxy() {
        if(this.proxy.isStarted()) this.proxy.stop();
        this.driver.close();
        this.driver.quit();
    }

    private SeleniumContainer(boolean showBrowser, final int port, final String protocol, final String host,String chromeDriverLocation ,CertificateUtil certificateUtil){

        this.baseUrl = String.format("%s://%s:%d", protocol, host, port);
        // start the proxy
        proxy = new BrowserMobProxyServer();
        proxy.setTrustAllServers(true);
        proxy.setMitmManager(certificateUtil.getCertificate());
        proxy.setTrustAllServers(true);
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT,
                CaptureType.RESPONSE_CONTENT);
        proxy.start(8068);

        // get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        try {
            //String hostIp = host;
            String hostIp = Inet4Address.getLocalHost().getHostAddress();
            log.info("proxyUrl = "+hostIp + ":" + proxy.getPort());
            seleniumProxy.setHttpProxy(hostIp + ":" + proxy.getPort());
            seleniumProxy.setSslProxy(hostIp + ":" + proxy.getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }



        // configure it as a desired capability
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

        ChromeOptions options = new ChromeOptions();
        options.addArguments( "--proxy-bypass-list=<-loopback>", "--disable-extensions");
        if(!showBrowser){
            options.addArguments( "--disable-gpu", "--headless", "--no-sandbox");
        }
        options.merge(capabilities);

        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(chromeDriverLocation))
                .usingAnyFreePort()
                .build();
        this.driver = new ChromeDriver(service, options);
    }

    public OrganizationAddPage getOrganizationAddPage() {
        if(this.organizationAddPage == null) {
            this.organizationAddPage = new OrganizationAddPage(driver, proxy, baseUrl);
            this.proxy.newHar(baseUrl);
        }
        return this.organizationAddPage;
    }
}
