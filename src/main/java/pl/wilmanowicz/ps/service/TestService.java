package pl.wilmanowicz.ps.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;

@Service
public class TestService {

    public void searchOffers(String city,String category){
        HttpClient httpClient = getHttpClient();
        HashSet<String> olxLinks = olxScrape(city, category, httpClient);
        olxLinks.stream()
                .forEach(System.out::println);
        HashSet<String> allegroLinks = allegroScrape(city,category);
        allegroLinks.stream()
                .forEach(System.out::println);

    }

    private HashSet<String> allegroScrape(String city, String category){
        String allegroPage = parseAllegroPage();
        System.out.println(allegroPage);
        try{
            WebClient webClient = getWebClient();
            Page page = webClient.getPage(allegroPage);
            WebResponse webResponse = page.getWebResponse();
            String content = webResponse.getContentAsString();
            webClient.close();
            System.out.println(content);
            String prefix = "https://allegro.pl/ogloszenie/";
            String suffix = "\"";
            return scrape(content,prefix,suffix);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String parseAllegroPage(){
        return "https://allegro.pl/kategoria/mieszkania-na-sprzedaz-112739?city=Gda%C5%84sk";
        //https://allegro.pl/kategoria/mieszkania-na-sprzedaz-112739?city=Gda%C5%84sk
    }

    private HashSet<String> olxScrape(String city, String category, HttpClient httpClient){
        String olxPage = parseOLXPage(category, city);
        HttpRequest httpRequest = getHttpRequest(olxPage);
        String content = getHttpResponse(httpClient, httpRequest);
        String prefix = "https://www.olx.pl/d/oferta/";
        String suffix = ".html";
        return scrape(content,prefix,suffix);
    }

    private String parseOLXPage(String category, String city){
        //return "https://www.olx.pl/" +  category + "/" + city;
        return "https://www.olx.pl/nieruchomosci/mieszkania/gdansk/";
        //https://www.olx.pl/nieruchomosci/mieszkania/gdansk/
    }

    private HashSet<String> scrape(String content,String prefix, String suffix){
        HashSet<String> linkSet = new HashSet<>();
        for (int i = 0; i < content.length(); i++) {
            i = content.indexOf(prefix, i);
            if (i < 0) {
                break;
            }
            linkSet.add(content.substring(i).split(suffix)[0] + suffix);
        }
        return linkSet;
    }

    private HttpClient getHttpClient(){
        return HttpClient.newHttpClient();
    }

    private HttpRequest getHttpRequest(String page) {
        try{
            return HttpRequest.newBuilder()
                    .uri(new URI(page))
                    .GET()
                    .build();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String getHttpResponse(HttpClient httpClient,HttpRequest httpRequest){
        try {
            HttpResponse<String> send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return send.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private WebClient getWebClient () {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        return webClient;
    }
}
