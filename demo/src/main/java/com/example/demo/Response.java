package com.example.demo;

import lombok.Data;

@Data
public class Response {

    private String[] vendorSkus;
    private String vendorId;

    public Response(String vendorId){
        this.vendorId = vendorId;
    }

}
