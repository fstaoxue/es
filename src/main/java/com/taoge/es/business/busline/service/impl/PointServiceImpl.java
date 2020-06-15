package com.taoge.es.business.busline.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taoge.es.business.busline.bean.Point;
import com.taoge.es.business.busline.dao.PointDao;
import com.taoge.es.business.busline.service.PointService;
import com.taoge.es.util.EsInsert;
import com.taoge.es.util.EsQuery;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private PointDao pointDao;

    @Override
    public List<Point> getPoints() {
        return pointDao.findAll(Sort.by(Sort.Order.desc("id")));
    }

    @Override
    public List<Point> getPointsWithTime(long sTime,long eTime) {
        return pointDao.findAllByCpBetween(sTime, eTime);
    }

    @Override
    public void insertToMysql(List<Point> points) {
        for(Point point:points){
            pointDao.save(point);
        }
    }


    @Override
    public void insertToEs(List<Point> points) {
        EsInsert.insert(points);
    }

    @Override
    public List<Point> queryPointsByES() {
        return EsQuery.getPoints("point","deafult","demo",0,System.currentTimeMillis()/1000);
    }

    @Override
    public List<Point> queryPointsByES(String coords) {
        List<GeoPoint> params=parseCoord(coords);
        return EsQuery.getPointsByBox("point","deafult","demo",0,System.currentTimeMillis()/1000,params);
    }

    @Override
    public List<Point> queryPointsByES(String coords, long sTime, long eTime) {
        List<GeoPoint> params=parseCoord(coords);
        return EsQuery.getPointsByBox("point","deafult","demo",sTime,eTime,params);
    }

    private List<GeoPoint> parseCoord(String coords){

        List<GeoPoint> points=new ArrayList<>();
        JSONArray array=JSON.parseArray(coords);
        JSONArray array1=array.getJSONArray(0);
        for(int i=0;i<array1.size();i++){
            JSONArray obj=array1.getJSONArray(i);
            double lng=Double.parseDouble(obj.getString(0));
            double lat=Double.parseDouble(obj.getString(1));
            GeoPoint temp=new GeoPoint(lat,lng);
            points.add(temp);
        }
        return points;
    }
}
