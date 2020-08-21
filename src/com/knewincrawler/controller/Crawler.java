package com.knewincrawler.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.knewincrawler.model.Noticia;

public class Crawler {

	private final String URL_SITE = "https://www.infomoney.com.br/mercados/";
	
	private WebDriver driver;
	
	// Lista de notícias encontradas na página inicial
	public ArrayList<Noticia> noticias;
	
	// Lista de notícias encontradas e já lidas na página inicial
	public ArrayList<WebElement> lidas;
	
	// Limitador da quantidade de noticias que serão buscadas pelo crawler
	private int TOTAL;
	
	// Acumulador do total de notícias já lidas
	private int CONTADOR = 0;
	
	public Crawler(int TOTAL) {
		this.TOTAL = TOTAL;
		this.noticias = new ArrayList<Noticia>();
		this.lidas = new ArrayList<WebElement>();
	}
		
	
	public void start() {
		config();
		getData();
		printNews();
		destroy();
	}
	

	private void destroy() {
		this.driver.quit();
	}
	

	public void getData() {
		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, 1000)");
		
		try {
			Thread.sleep(5000);
		
	        ArrayList<WebElement> links = new ArrayList<WebElement>(driver.findElements(By.xpath("//span[contains(@class, 'hl-title') and contains(@class, 'hl-title-2')]")));

			while (CONTADOR < TOTAL && !links.isEmpty()) {
				
				
				for (WebElement link: links) {
					
					if (CONTADOR >= TOTAL) {
						break;
					}
					
					goToElement(link);
					link.click();
					
					ArrayList<String> abas = new ArrayList<String>(driver.getWindowHandles());
					this.driver.switchTo().window(abas.get(1));
					
					Thread.sleep(3000);
					Noticia noticia = new Noticia();
					
					noticia.setUrl(this.driver.getCurrentUrl());
					
					WebElement tituloElement = driver.findElement(By.xpath("//h1[contains(@class, 'page-title-1')]"));
					goToElement(tituloElement);
					noticia.setTitulo(tituloElement.getText());
					
					WebElement subtituloElement = driver.findElement(By.xpath("//p[contains(@class, 'article-lead')]"));
					goToElement(subtituloElement);
					noticia.setSubtitulo(subtituloElement.getText());
					
					WebElement autorElement = driver.findElement(By.xpath("//span[contains(@class, 'author-name')]"));
					goToElement(autorElement);
					String autor = autorElement.getText();
					autor = autor.substring(0,4).equals("Por ") ? autor.substring(4, autor.length()) : autor;
					noticia.setAutor(autor);
					
					WebElement dataElement = driver.findElement(By.xpath("//time[contains(@class, 'entry-date') and contains(@class, 'published')]"));
					goToElement(dataElement);
					OffsetDateTime dt = OffsetDateTime.parse(dataElement.getAttribute("datetime"));
					Date date = new Date(dt.toInstant().toEpochMilli());
					String dataFormatada = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
					noticia.setDataPublicacao(dataFormatada);
					
					
					// Conteúdo
					
					
					this.lidas.add(link);
					this.noticias.add(noticia);
					this.driver.close();
					this.driver.switchTo().window(abas.get(0));
					CONTADOR = CONTADOR + 1;
	
				}
				
				WebElement botao = this.driver.findElement(By.xpath("/html/body/div[4]/div[4]/div/div/div[11]/span/button"));
				goToElement(botao);
				Thread.sleep(2000);
				
				JavascriptExecutor ex = (JavascriptExecutor)driver;
				ex.executeScript("arguments[0].click();", botao);
				
				Thread.sleep(6000);
				links = new ArrayList<WebElement>(this.driver.findElements(By.xpath("//span[contains(@class, 'hl-title') and contains(@class, 'hl-title-2')]")));
				links = new ArrayList<WebElement>(links.stream().filter(n -> !lidas.contains(n)).collect(Collectors.toList()));
				goToElement(links.get(0));
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			destroy();
		}

	}
	
	
	private void config() {
		Properties prop=new Properties();
		try {
			FileInputStream ip = new FileInputStream("src/config.properties");
			prop.load(ip);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String cd = prop.getProperty("chromedriverpath");
		System.setProperty("webdriver.chrome.driver", cd);
		
		ChromeOptions options = new ChromeOptions();
		
		//DesiredCapabilities capabilities = new DesiredCapabilities().chrome();   //
		//capabilities.setAcceptInsecureCerts(true);    //
		//capabilities.setJavascriptEnabled(true);     //
		
		options.setCapability(HtmlUnitDriver.JAVASCRIPT_ENABLED, true);
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		//options.setCapability(ChromeOptions.CAPABILITY, capabilities);  //
		//options.setCapability("browserName", "htmlunit");    //
		options.setCapability(CapabilityType.SUPPORTS_FINDING_BY_CSS, true);
		
		driver = new ChromeDriver/*HtmlUnitDriver*/(options);
		
		driver.manage().window().maximize();
		driver.get(URL_SITE);
	}
	
	
	public void printNews() {
	    
		this.noticias.forEach(n -> System.out.println(String.format(
						"Url: %s\nData da publicação: %s\nTítulo: %s\nSubtítulo: %s\nAutor: %s\n",
								n.getUrl(), n.getDataPublicacao().toString(), n.getTitulo(), n.getSubtitulo(), n.getAutor()
								)
							)
						);
	}
	
	private void goToElement(WebElement element) {
		((JavascriptExecutor) this.driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}
	
	
}
