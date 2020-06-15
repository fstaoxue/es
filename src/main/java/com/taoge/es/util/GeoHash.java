package com.taoge.es.util;

import java.util.*;

/**
 * Class GeoHash
 * [功能描述]
 *
 * @author: WJQ0000621
 * @date: 2017/7/13 16:01
 * @email: wang.jingqi@sinovatio.com
 */

public class GeoHash {

    public static final char[] BASE32 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    public static final Map<Character, Integer> INV_BASE32 = new HashMap<Character, Integer>();

    public static final double LONG_7 = 0.001373291015625;
    public static final double LATI_7 = 0.001373291015625;

    static {
        for (int i = 0; i < BASE32.length; i++) {
            INV_BASE32.put(BASE32[i], i);
        }
    }

    /**
     * 6位geohash 单个网格的长度
     */
    public static final double LENGTH_6 = 1064.3;
    /**
     * 6位geohash 单个网格的宽度
     */
    public static final double WIDTH_6 = 611.5;

    /**
     * 7位geohash 单个网格的长度
     */
    public static final double LENGTH_7 = 133.0;
    /**
     * 7位geohash 单个网格的宽度
     */
    public static final double WIDTH_7 = 152.9;

    /**
     * 6位geohash 近邻的距离
     */
    private static double threshold6 = 306;

    /**
     * 获取geohash值
     *
     * @param longitude
     * @param latitude
     * @param bits
     * @return
     */
    public static String geohash(double longitude, double latitude, int bits) {
        int length = bits * 5 / 2;
        int longitudeLength = length;
        boolean b = bits % 2 != 0;
        if (b) {
            longitudeLength++;
        }

        char[] longitudeArray = toBinary(-180, 180, longitude, longitudeLength);
        char[] latitudeArray = toBinary(-90, 90, latitude, length);

        //LOG.debug("longitude: {}", Arrays.asList(longitudeArray));
        //LOG.debug("latitude: {}", Arrays.asList(latitudeArray));
        StringBuffer geoHash = new StringBuffer(longitudeLength + length);
        for (int i = 0; i < length; i++) {
            geoHash.append(longitudeArray[i]);
            geoHash.append(latitudeArray[i]);
        }
        if (b) {
            geoHash.append(longitudeArray[length]);
        }

        //LOG.debug("binary geoHash:{}", geoHash.toString());

        int i = 0;
        StringBuffer result = new StringBuffer(bits);
        while (i < length * 2) {
            int j = i + 5;
            int index = Integer.parseInt(geoHash.substring(i, j), 2);
            //LOG.debug("str[{},{}]:{}, index:{}", i, j, geoHash.substring(i, j), index);
            result.append(BASE32[index]);
            i = j;
        }
        return result.toString();
    }

    /**
     * @param biLatitude
     * @param biLongitude
     * @param bits
     * @return
     */
    private static String encode(String biLatitude, String biLongitude, int bits) {
        int length = bits * 5 / 2;
        int longitudeLength = length;
        boolean b = bits % 2 != 0;
        if (b) {
            longitudeLength++;
        }
        StringBuffer geoHash = new StringBuffer(longitudeLength + length);
        for (int i = 0; i < length; i++) {
            geoHash.append(biLongitude.charAt(i));
            geoHash.append(biLatitude.charAt(i));
        }
        if (b) {
            geoHash.append(biLongitude.charAt(length));
        }

        //LOG.debug("geohash:{}", geoHash.toString());
        int i = 0;
        StringBuffer result = new StringBuffer(bits);
        while (i < length * 2) {
            int j = i + 5;
            int index = Integer.parseInt(geoHash.substring(i, j), 2);
            result.append(BASE32[index]);
            i = j;
        }
        return result.toString();
    }

    /**
     * @param geohash
     * @return
     */
    private static String decodeToBinary(String geohash) {
        StringBuffer result = new StringBuffer(geohash.length() * 5);
        for (char c : geohash.toCharArray()) {
            String bits = Integer.toBinaryString(INV_BASE32.get(c));
            for (int i = 0; i < 5 - bits.length(); i++) {
                result.append('0');
            }
            result.append(bits);
        }
        return result.toString();
    }

    /**
     * @param lower
     * @param upper
     * @param value
     * @param length
     * @return
     */
    private static char[] toBinary(double lower, double upper, double value, int length) {
        char[] binaryArray = new char[length];
        double min = lower;
        double max = upper;
        for (int i = 0; i < length; i++) {
            double mid = (max + min) / 2;
            //LOG.info("interval-{}:[{},{},{}]", i, min, mid, max);
            if (value < mid) {
                binaryArray[i] = '0';
                max = mid;
            } else {
                binaryArray[i] = '1';
                min = mid;
            }
        }
        return binaryArray;
    }

    /**
     * 求与当前geohash相邻的8个格子的geohash值。
     *
     * @param geohash
     * @return string数组，周围格子的geohash值
     */
    public static String[] expand8(String geohash) {
        int bits = geohash.length();
        String bCoordinate = decodeToBinary(geohash);//当前geohash值对应的二进制串
        //LOG.debug("geohash:{} ==> bits:{}", geohash, bCoordinate);

        StringBuilder bLat = new StringBuilder();
        StringBuilder bLon = new StringBuilder();

        for (int i = 0; i < bCoordinate.length(); i++) {
            if ((i % 2) == 0) {
                bLon.append(bCoordinate.charAt(i));
            } else {
                bLat.append(bCoordinate.charAt(i));
            }
        }
        String lat = bLat.toString();
        String lon = bLon.toString();

        String downLat = calculate(lat, -1);
        String upLat = calculate(lat, 1);
        String leftLon = calculate(lon, -1);
        String rightLon = calculate(lon, 1);

        return new String[]{encode(upLat, leftLon, bits), encode(upLat, lon, bits), encode(upLat, rightLon, bits),
                encode(lat, leftLon, bits), geohash, encode(lat, rightLon, bits), encode(downLat, leftLon, bits),
                encode(downLat, lon, bits), encode(downLat, rightLon, bits)};
    }

    /**
     * 求与当前geohash相邻的(2*num+1)^2-1个格子的geohash值。
     *  1001352GJH
     * @param geohash
     * @return string数组，周围格子的geohash值
     */
    public static Set<String> expandNum(String geohash, int num) {
        int bits = geohash.length();
        String bCoordinate = decodeToBinary(geohash);//当前geohash值对应的二进制串
        //LOG.debug("geohash:{} ==> bits:{}", geohash, bCoordinate);

        StringBuilder bLat = new StringBuilder();
        StringBuilder bLon = new StringBuilder();

        for (int i = 0; i < bCoordinate.length(); i++) {
            if ((i % 2) == 0) {
                bLon.append(bCoordinate.charAt(i));
            } else {
                bLat.append(bCoordinate.charAt(i));
            }
        }
        String lat = bLat.toString();
        String lon = bLon.toString();

        Set<String> latList = new HashSet<>();
        Set<String> lonList = new HashSet<>();
        latList.add(lat);
        lonList.add(lon);

        String top = lat;
        String bottom = lat;
        String left = lon;
        String right = lon;
        for (int i = 0; i < num; i++) {
            top = calculate(top, 1);
            bottom = calculate(bottom, -1);
            latList.add(top);
            latList.add(bottom);

            left = calculate(left, -1);
            right = calculate(right, 1);
            lonList.add(left);
            lonList.add(right);
        }

        Set<String> endGeo = new HashSet<>();
        for (String tmpLat : latList) {
            for (String tmpLon : lonList) {
                String geo = encode(tmpLat, tmpLon, bits);
                endGeo.add(geo);
            }
        }

        return endGeo;
    }

    /**
     * 求与当前geohash相邻的(2*lonNum+1)^(2*latNum+1)-1个格子的geohash值。
     * 1001352GJH
     *
     * @param geohash
     * @return
     */
    public static Set<String> expandNum(String geohash, int lonNum, int latNum) {
        int bits = geohash.length();
        String bCoordinate = decodeToBinary(geohash);//当前geohash值对应的二进制串
        //LOG.debug("geohash:{} ==> bits:{}", geohash, bCoordinate);

        StringBuilder bLat = new StringBuilder();
        StringBuilder bLon = new StringBuilder();

        for (int i = 0; i < bCoordinate.length(); i++) {
            if ((i % 2) == 0) {
                bLon.append(bCoordinate.charAt(i));
            } else {
                bLat.append(bCoordinate.charAt(i));
            }
        }
        String lat = bLat.toString();
        String lon = bLon.toString();

        Set<String> latList = new HashSet<>();
        Set<String> lonList = new HashSet<>();
        latList.add(lat);
        lonList.add(lon);

        String top = lat;
        String bottom = lat;
        String left = lon;
        String right = lon;
        for (int i = 0; i < lonNum; i++) {
            left = calculate(left, -1);
            right = calculate(right, 1);
            lonList.add(left);
            lonList.add(right);
        }

        for (int i = 0; i < latNum; i++) {
            top = calculate(top, 1);
            bottom = calculate(bottom, -1);
            latList.add(top);
            latList.add(bottom);
        }

        Set<String> endGeo = new HashSet<>();
        for (String tmpLat : latList) {
            for (String tmpLon : lonList) {
                String geo = encode(tmpLat, tmpLon, bits);
                endGeo.add(geo);
            }
        }

        return endGeo;
    }

    /**
     * 求与当前geohash相邻的8个格子的geohash值。
     *
     * @param geohash
     * @return string数组，周围格子的geohash值
     */
    public static String[] neighborGrid(String geohash) {
        int bits = geohash.length();
        String bCoordinate = decodeToBinary(geohash);//当前geohash值对应的二进制串

        StringBuilder bLat = new StringBuilder();
        StringBuilder bLon = new StringBuilder();

        for (int i = 0; i < bCoordinate.length(); i++) {
            if ((i % 2) == 0) {
                bLon.append(bCoordinate.charAt(i));
            } else {
                bLat.append(bCoordinate.charAt(i));
            }
        }
        String lat = bLat.toString();
        String lon = bLon.toString();

        String downLat = calculate(lat, -1);
        String upLat = calculate(lat, 1);
        String leftLon = calculate(lon, -1);
        String rightLon = calculate(lon, 1);

        return new String[]{encode(upLat, leftLon, bits), encode(upLat, lon, bits), encode(upLat, rightLon, bits),
                encode(lat, leftLon, bits), encode(lat, rightLon, bits), encode(downLat, leftLon, bits),
                encode(downLat, lon, bits), encode(downLat, rightLon, bits)};
    }

    /**
     * 计算当前格子左右（上下）格子的经（纬）度值二进制串
     *
     * @param coordinate 当前格子的经/纬度值
     * @param i          偏移量
     * @return
     */
    private static String calculate(String coordinate, int i) {
        int length = coordinate.length();
        String result = Integer.toBinaryString((Integer.valueOf(coordinate, 2) + i) + (1 << length)).substring(1);
        if (result.length() != length) {
            return null;
        } else {
            return result;
        }
    }

    /**
     * geohash反算经纬度
     *
     * @param geohash
     * @return
     */
    public static double[] decode(String geohash) {

        StringBuilder buffer = new StringBuilder();
        for (char c : geohash.toCharArray()) {

            int i = INV_BASE32.get(c) + 32;//加32以避免string的第一个字符为0
            buffer.append(Integer.toString(i, 2).substring(1));
        }

        BitSet lonset = new BitSet();
        BitSet latset = new BitSet();

        int numbits = 30;//就是用来遍历用的临时值
        //even bits
        int j = 0;
        for (int i = 0; i < numbits * 2; i += 2) {
            boolean isSet = false;
            if (i < buffer.length()) {
                isSet = buffer.charAt(i) == '1';
            }
            lonset.set(j++, isSet);
        }

        //odd bits
        j = 0;
        for (int i = 1; i < numbits * 2; i += 2) {
            boolean isSet = false;
            if (i < buffer.length()) {
                isSet = buffer.charAt(i) == '1';
            }
            latset.set(j++, isSet);
        }

        double lon = decode(lonset, -180, 180);
        double lat = decode(latset, -90, 90);

        return new double[]{lat, lon};

    }

    /**
     * @param bs
     * @param floor
     * @param ceiling
     * @return
     */
    private static double decode(BitSet bs, double floor, double ceiling) {
        double mid = 0;
        for (int i = 0; i < bs.length(); i++) {
            mid = (floor + ceiling) / 2;
            if (bs.get(i)) {
                floor = mid;
            } else {
                ceiling = mid;
            }
        }
        return mid;
    }

    public static void main(String[] args) {
        //System.out.println(AlgorithmUtils.getDistance(106.5234375, 29.619140625, 106.5673828125, 29.6630859375));
        /*double longitude1 = 105.76263427734375;
        double latitude1 = 29.51202392578125;*/
        double longitude2 = 106.55979919433594;
        double latitude2 = 29.641340255737305;
        //  System.out.println(GeoHash.rangeGeoHash(longitude1,latitude1,400,7));
        // String geohash = GeoHash.geohash(longitude2, latitude2, 5);
        // LOG.info("{}", geohash);
/*
        expand(geohash, longitude2, latitude2);*/
        //geohash = GeoHash.geohash(longitude2, latitude2, 6);
        //String[] neighbors = g.expand8("wm7b97");
        //LOG.info("{}", geohash);

        /*double distance = MyUtils.getDistance(longitude1, latitude1, longitude1, latitude2);
        LOG.info("{}", distance);
        distance = MyUtils.getDistance(longitude2, latitude1, longitude2, latitude2);
        LOG.info("{}", distance);
        distance = MyUtils.getDistance(longitude1, latitude1, longitude2, latitude1);
        LOG.info("{}", distance);
        distance = MyUtils.getDistance(longitude1, latitude2, longitude2, latitude2);
        LOG.info("{}", distance);
        distance = MyUtils.getDistance(longitude1, latitude1, longitude2, latitude2);
        LOG.info("{}", distance);*/
    }
}