package com.taoge.es.util;

import com.taoge.es.business.busline.bean.Point;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
public class EsInsert {

    private static BulkProcessor bulkProcessor;

    static {
        bulkProcessor = BulkProcessor.builder(EsUtil.getClient(), new BulkListener())
                .setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(256, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(3))
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();
    }

    public static void insert(List<Point> points){
        for(Point point:points){
            double lat=point.getLat();
            double lon=point.getLng();
            long cp=point.getCp();
            String info=point.getInfo();
            String id=GeoHash.geohash(lon,lat,6)+"_"+cp+"_"+info;
            try {
                XContentBuilder builder= XContentFactory.jsonBuilder();
                builder.startObject()
                        .startObject("location").field("lat",lat).field("lon",lon).endObject()
                        .field("cp",cp)
                        .field("info",info)
                        .endObject();
                IndexRequest request=new IndexRequest("point","deafult",id).source(builder);
                bulkProcessor.add(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bulkProcessor.flush();
    }

    private static class BulkListener implements BulkProcessor.Listener{

        @Override
        public void beforeBulk(long l, BulkRequest bulkRequest) {

        }

        @Override
        public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {

        }

        @Override
        public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {

        }
    }

}


