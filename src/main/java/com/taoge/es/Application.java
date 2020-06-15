package com.taoge.es;

import com.taoge.es.util.EsIndex;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
        //EsIndex.creatIndex();
    }
}
