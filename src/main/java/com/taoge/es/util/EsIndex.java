package com.taoge.es.util;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
public class EsIndex {

    public static CreateIndexResponse creatIndex() {
        CreateIndexResponse response = null;
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject()
                    .startObject("properties")
                    .startObject("location").field("type", "geo_point").field("doc_values", false).endObject()
                    .startObject("cp").field("type", "long").endObject()
                    .startObject("info").field("type", "text").endObject()
                    .endObject()
                    .endObject();
            response=EsUtil.getClient().admin().indices()
                    .prepareCreate("point")
                    .addMapping("deafult",builder)
                    .execute().actionGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}
