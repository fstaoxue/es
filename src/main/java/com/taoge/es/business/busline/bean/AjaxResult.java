package com.taoge.es.business.busline.bean;

import java.util.HashMap;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
public class AjaxResult extends HashMap<String,Object> {

    public static AjaxResult success(Object data){
        return produce(200,data,"success");
    }

    public static AjaxResult error(){
        return produce(404,"","error");
    }

    private static AjaxResult produce(int code,Object data,String msg){
        AjaxResult result=new AjaxResult();
        result.put("code",code);
        result.put("data",data);
        result.put("msg",msg);
        return result;
    }
}
