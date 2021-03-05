package com.example.travelbookjava.model;

import java.io.Serializable;

public class Place implements Serializable {

    public String name;
    public Double latitude;
    public Double longitude;
    public Double id;

    public Place(String name,Double latitude,Double longitude,Double id){
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;
        this.id=id;
    }
    public Place(String name,Double latitude,Double longitude){
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;

    }

}
