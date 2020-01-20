package com.example.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/rest/hello")
    public String getVendorSkus() throws Exception {
        //Simulate Doing some piece of work which blocks thread
//        Thread.sleep(5000);
        return "[\"j8441\",\"A12345\"]";
    }
}
