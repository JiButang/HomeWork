package com.newemployee;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */
@MapperScan("com.newemployee.dao")
@SpringBootApplication(scanBasePackages = {"com.newemployee"})
@RestController
public class App
{
    @RequestMapping("/test")
    public String test(){
        return "Hello World";
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SpringApplication.run(App.class,args);
    }
}
