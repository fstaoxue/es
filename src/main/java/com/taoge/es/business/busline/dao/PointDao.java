package com.taoge.es.business.busline.dao;

import com.taoge.es.business.busline.bean.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
public interface PointDao extends JpaRepository<Point,Integer>{

    List<Point> findAllByCpBetween(long sTime,long eTime);
}
