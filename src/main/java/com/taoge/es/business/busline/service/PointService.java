package com.taoge.es.business.busline.service;

import com.taoge.es.business.busline.bean.Point;

import java.util.List;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
public interface PointService {

    List<Point> getPoints();

    List<Point> getPointsWithTime(long sTime,long eTime);

    void insertToMysql(List<Point> points);

    void insertToEs(List<Point> points);

    List<Point> queryPointsByES();

    List<Point> queryPointsByES(String coords,long sTime, long eTime);

    List<Point> queryPointsByES(String coords);

}
