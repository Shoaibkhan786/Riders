package com.example.muhammadkhan.ridersapp.Models;

import java.io.Serializable;

/**
 * Created by Muhammad Khan on 09/05/2018.
 */

public class Chat implements Serializable {
    String name;
    String message;

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
    public Chat(){
        //Empty construtor for chat
    }
    public Chat(String name,String message){
        this.name=name;
        this.message=message;
    }
}
