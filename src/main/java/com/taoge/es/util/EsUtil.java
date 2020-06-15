package com.taoge.es.util;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
public class EsUtil {

    private static TransportClient client=null;

    static {
        init();
    }


    private static void init(){
        try {
            Settings settings = Settings.builder().put("cluster.name", "elasticsearch").put("client.transport.sniff", false).build();
            TransportClient transportClient=new PreBuiltTransportClient(settings);
            transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName("114.55.64.105"), 9300));
            client =transportClient;
        } catch (Exception e) {
            System.out.println("初始化es连接异常。。。。");
            e.printStackTrace();
        }
    }

    public static TransportClient getClient(){
        if(client==null){
            init();
        }
        return client;
    }

    public static void Close(){
        if(client!=null){
            client.close();
        }
    }

    public static List<String> getAllIndexs(){
        if(client!=null){
            ClusterState state = client.admin().cluster().prepareState().execute().actionGet().getState();
            return Arrays.asList(state.getMetaData().getConcreteAllIndices());
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        System.out.println(getAllIndexs());
    }


}
