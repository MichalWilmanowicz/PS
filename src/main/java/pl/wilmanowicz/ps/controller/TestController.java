package pl.wilmanowicz.ps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wilmanowicz.ps.service.TestService;

@RestController
@RequestMapping("/")
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping
    public void getOffers(){
        //String city,String category
        String city = "gdansk";
        String category = "nieruchomosci/mieszkania";
        testService.searchOffers(city,category);
    }
}
