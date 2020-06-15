package com.taoge.es.business.busline.bean;

import javax.persistence.*;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
@Entity
@Table(name = "point")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name="lat")
    private double lat;

    @Column(name="lng")
    private double lng;

    @Column(name = "cp")
    private long cp;

    @Column(name="info")
    private String info;

    @Transient
    private String rowKey;

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getCp() {
        return cp;
    }

    public void setCp(long cp) {
        this.cp = cp;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
