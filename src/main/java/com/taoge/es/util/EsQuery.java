package com.taoge.es.util;

import com.taoge.es.business.busline.bean.Point;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.GeoPolygonQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
public class EsQuery {

    public static List<Point> getPoints(String index, String type, String info, long sTime, long eTime) {
        System.out.println(EsUtil.getAllIndexs());
        List<Point> points = new ArrayList<>();
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("info", info))
                .must(QueryBuilders.rangeQuery("cp").gt(sTime).lt(eTime));
        SearchResponse response = EsUtil.getClient().prepareSearch().setIndices(index).setTypes(type).setQuery(queryBuilder)
                .addSort("cp", SortOrder.ASC).setScroll(new TimeValue(60000))
                .setSize(1000).execute().actionGet();
        if (response != null) {
            SearchHits searchHits = response.getHits();
            SearchHit[] hits = searchHits.getHits();
            for (int i = 0; i < hits.length; i++) {
                SearchHit hit = hits[i];
                Point point = hitToPoint(hit);
                points.add(point);
            }
        }
        return points;
    }

//    private static Point hitToPoint(SearchHit hit) {
//        Map<String, Object> map = hit.getSourceAsMap();
//        Class clazz = Point.class;
//        Point point = new Point();
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            try {
//                Field field = clazz.getDeclaredField(key);
//                if (field != null) {
//                    Class fieldType = field.getType();
//                    String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
//                    Method method = clazz.getDeclaredMethod(methodName, fieldType);
//                    if (method != null) {
//                        method.invoke(point, value);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return point;
//    }

    public static List<Point> getPointsByBox(String index, String type, String info,long sTime,long eTime,
                                             List<GeoPoint> geoPoints) {
        List<Point> points = new ArrayList<>();

        GeoPolygonQueryBuilder gpqb = QueryBuilders.geoPolygonQuery("location", geoPoints);
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(gpqb)
                .must(QueryBuilders.rangeQuery("cp").gt(sTime).lt(eTime));
        SearchResponse response = EsUtil.getClient().prepareSearch().setIndices(index).setTypes(type).setQuery(queryBuilder)
                .addSort("cp", SortOrder.ASC).setScroll(new TimeValue(60000))
                .setSize(1000).execute().actionGet();
        if (response != null) {
            SearchHits searchHits = response.getHits();
            SearchHit[] hits = searchHits.getHits();
            for (int i = 0; i < hits.length; i++) {
                SearchHit hit = hits[i];
                Point point = hitToPoint(hit);
                points.add(point);
            }
        }
        return points;
    }

    private static Point hitToPoint(SearchHit hit){
        Point point=new Point();
        Map<String,Object> map=hit.getSourceAsMap();
        if(map.containsKey("cp")){
            point.setCp(Long.parseLong(map.get("cp").toString()));
        }
        if(map.containsKey("info")){
            point.setInfo(map.get("info").toString());
        }
        if(map.containsKey("location")){
            Map<String,Double> location=(HashMap<String,Double>)map.get("location");
            point.setLat(location.get("lat"));
            point.setLng(location.get("lon"));
        }
        point.setRowKey(hit.getId());
        return point;
    }

}
