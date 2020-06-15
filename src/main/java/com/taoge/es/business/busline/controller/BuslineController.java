package com.taoge.es.business.busline.controller;

import com.alibaba.fastjson.JSON;
import com.taoge.es.business.busline.bean.AjaxResult;
import com.taoge.es.business.busline.bean.Point;
import com.taoge.es.business.busline.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
@Controller
@RequestMapping("/es")
public class BuslineController {

    @Autowired
    private PointService pointService;

    @PostMapping("/exportMysql")
    @ResponseBody
    public AjaxResult exportMysql(String points){
        try{
            List<Point> pointList= JSON.parseArray(points,Point.class);
            pointService.insertToMysql(pointList);
            return AjaxResult.success("");
        }catch (Exception e){
            e.printStackTrace();
        }
        return AjaxResult.error();
    }

    @PostMapping("/exportEs")
    @ResponseBody
    public AjaxResult exportEs(String points){
        try{
            List<Point> pointList= JSON.parseArray(points,Point.class);
            pointService.insertToEs(pointList);
            return AjaxResult.success("");
        }catch (Exception e){
            e.printStackTrace();
        }
        return AjaxResult.error();
    }

    @GetMapping("/listByMysql")
    @ResponseBody
    public AjaxResult listByMysql(){
        try{
            List<Point> points=pointService.getPoints();
            return AjaxResult.success(points);
        }catch (Exception e){
            e.printStackTrace();
        }
        return AjaxResult.error();
    }

    @GetMapping("/listByMysqlWithTime")
    @ResponseBody
    public AjaxResult listByMysqlWithTime(long sTime,long eTime){
        try{
            List<Point> points=pointService.getPointsWithTime(sTime, eTime);
            return AjaxResult.success(points);
        }catch (Exception e){
            e.printStackTrace();
        }
        return AjaxResult.error();
    }

    @GetMapping("/listByEs")
    @ResponseBody
    public AjaxResult listByEs(){
        try{
            List<Point> points=pointService.queryPointsByES();
            return AjaxResult.success(points);
        }catch (Exception e){
            e.printStackTrace();
        }
        return AjaxResult.error();
    }

    @GetMapping("/listByEsWithBox")
    @ResponseBody
    public AjaxResult listByEsWithBox(String coordJson){
        try{
            List<Point> points=pointService.queryPointsByES(coordJson);
            return AjaxResult.success(points);
        }catch (Exception e){
            e.printStackTrace();
        }
        return AjaxResult.error();
    }

    @GetMapping("/listByEsWithTime")
    @ResponseBody
    public AjaxResult listByEsWithTime(String coordJson,long sTime,long eTime){
        try{
            List<Point> points=pointService.queryPointsByES(coordJson,sTime,eTime);
            return AjaxResult.success(points);
        }catch (Exception e){
            e.printStackTrace();
        }
        return AjaxResult.error();
    }

}
