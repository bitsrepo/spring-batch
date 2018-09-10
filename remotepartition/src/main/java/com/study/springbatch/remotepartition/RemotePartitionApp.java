package com.study.springbatch.remotepartition;


import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */


@SpringBootApplication
@EnableBatchProcessing
public class RemotePartitionApp {
    
    public static void main(String[] args)
    {
        System.out.println("hello world");
        SpringApplication.run(RemotePartitionApp.class, args);
    }
    
}
