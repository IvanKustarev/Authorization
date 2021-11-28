package com.example.authorization.kafkaProduser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Scanner;

@Service
public class Sender {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String msg) {
        kafkaTemplate.send("first", msg);
    }


    @PostConstruct
    private void sending(){
        Scanner scanner = new Scanner(System.in);

        while (true){
            String str = scanner.nextLine();
            sendMessage(str);
        }
    }
}
