package com.example.kushal.letschat;

/**
 * Created by Rohan on 9/1/2015.
 */
public class Chat {

    private String name;
    private String chat;

    public Chat(String name,String chat){
        this.name = name;
        this.chat = chat;
    }

    public Chat(){
        // necessary for Firebase's deserializer
    }

    public String getChat() {
        return chat;
    }


    public String getName() {
        return name;
    }
}