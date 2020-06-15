<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <base href="${req.contextPath }/">
    <link href="css/bootstrap.min.css" rel="stylesheet" />
    <link href="js/openlayer/ol.css" rel="stylesheet"/>
    <link href="layui/css/layui.css" rel="stylesheet"/>
</head>
<body>

<div class="container">
    <div >
        <div class="row">
            <button type="button" class="btn btn-primary" id="queryEsAll">查询全部</button>
            <button type="button" class="btn btn-primary" id="queryEsAllWithBox">框选查全部</button>
            <button type="button" class="btn btn-primary" id="queryEsAllWithBoxAndTime">框选带时间查</button>
            <input id="startDate" placeholder="开始时间" class="Wdate" type="text" onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd HH:mm:ss'})">
            <input id="endDate" placeholder="结束时间" class="Wdate" type="text" onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd HH:mm:ss'})">
        </div>

        <h4>框选经纬度信息</h4>
        <textarea id="boxInfo" cols="80" rows="10"></textarea>
        <div class="row">
            <div id="map" style="height: 400px;"></div>

        </div>

        <div style="margin-top:20px;margin-bottom:20px;max-height: 300px;overflow-y: scroll;">
            <table id="pointTable" class="table table-bordered">
                <thead>
                <th>序号</th>
                <th>rowkey</th>
                <th>纬度</th>
                <th>经度</th>
                <th>时间</th>
                </thead>
                <tbody >

                </tbody>
            </table>
        </div>



    </div>
</div>
<script src="js/jquery-1.9.1.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<script src="js/openlayer/ol.js" type="text/javascript"></script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script src="layui/layui.js" type="text/javascript"></script>
<script type="text/javascript">


    var layer;
    layui.use('layer', function(){
        layer = layui.layer;
    });

    $(function () {
        init_btn_event();
        init_map();
        addInteraction();
    });
    function init_btn_event(){
        $("#queryEsAll").click(function(){
            clear();
            $.ajax({
                url:"/es/es/listByEs",
                type:'get',
                dataType:'json',
                timeout:60000,
                success:function(data){
                    if(data&&data.code=="200"){
                        var result=data.data;
                        $("#pointTable tbody").html(getTableHtml(result));
                    }
                },
                error:function(){
                    layer.msg("查询Es失败");
                }
            })
        });
        $("#queryEsAllWithBox").click(function(){
            clear();
            if(!Array.isArray(coords)){
                layer.msg("框选参数不正确");
               return;
            }
            $.ajax({
                url:"/es/es/listByEsWithBox",
                type:'get',
                dataType:'json',
                data:{
                    coordJson:JSON.stringify(coords)
                },
                timeout:60000,
                success:function(data){
                    if(data&&data.code=="200"){
                        var result=data.data;
                        $("#pointTable tbody").html(getTableHtml(result));
                    }
                },
                error:function(){
                    layer.msg("查询Es失败");
                }
            })
        });
        $("#queryEsAllWithBoxAndTime").click(function(){
            clear();
            if(!Array.isArray(coords)){
                layer.msg("框选参数不正确");
                return;
            }
            var startDate=$("#startDate").val();
            var endDate=$("#endDate").val();
            var sTime=Math.ceil(new Date(startDate).getTime()/1000);
            var eTime=Math.ceil(new Date(endDate).getTime()/1000);
            $.ajax({
                url:"/es/es/listByEsWithTime",
                type:'get',
                dataType:'json',
                data:{
                    sTime:sTime,
                    eTime:eTime,
                    coordJson:JSON.stringify(coords)
                },
                timeout:60000,
                success:function(data){
                    if(data&&data.code=="200"){
                        var result=data.data;
                        $("#pointTable tbody").html(getTableHtml(result));
                    }
                },
                error:function(){
                    layer.msg("查询ES失败");
                }
            })
        });
    }

    function getTableHtml(points){
        var str="";
        for(var i in points){
            var temp=points[i];
            if(i==0){
                map.getView().setCenter([Number(temp.lng),Number(temp.lat)]);
            }
            temp.id=i;
            str+="<tr>"
            str+="<td>";
            str+=temp.id;
            str+="</td>";
            str+="<td>";
            str+=temp.rowKey;
            str+="</td>";
            str+="<td>";
            str+=temp.lat;
            str+="</td>";
            str+="<td>";
            str+=temp.lng;
            str+="</td>";
            str+="<td>";
            str+=dateFormat("YYYY-mm-dd HH:MM:SS", new Date(Number(temp.cp)*1000));
            str+="</td>";
            str+="</tr>";
            drawMarker(temp);

        }
        return str;
    }

    function dateFormat(fmt, date) {
        var ret;
        var opt = {
            "Y+": date.getFullYear().toString(),        // 年
            "m+": (date.getMonth() + 1).toString(),     // 月
            "d+": date.getDate().toString(),            // 日
            "H+": date.getHours().toString(),           // 时
            "M+": date.getMinutes().toString(),         // 分
            "S+": date.getSeconds().toString()          // 秒
            // 有其他格式化字符需求可以继续添加，必须转化成字符串
        };
        for (var k in opt) {
            ret = new RegExp("(" + k + ")").exec(fmt);
            if (ret) {
                fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
            };
        };
        return fmt;
    }


    var source = new ol.source.Vector({});

    var vector = new ol.layer.Vector({
        source: source,
        style: new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: 'yellow',
                width: 4
            }),
            fill: new ol.style.Fill({
                color: 'blue'
            })
        })
    });

    var gaode=new ol.layer.Tile({
        source:new ol.source.XYZ({
            tileUrlFunction:function (coord) {
                var z=coord[0];
                var x=coord[1];
                var y=-coord[2]-1;
                var url='http://mt2.google.cn/vt/lyrs=m&scale=2&hl=zh-CN&gl=cn';
                url+="&x=" + x+ "&y=" + y+ "&z="+z;
                return url;
            }
        })
    });

    function init_map(){
        map=new ol.Map({
            layers:[gaode,vector],
            view:new ol.View({
                center:[118.7,32.0],
                projection:'EPSG:4326',
                zoom:14
            }),
            target:'map'
        });
    }


    function drawMarker(point){
        var marker = new ol.Feature({
            geometry:new ol.geom.Point([Number(point.lng),Number(point.lat)])
        });

        marker.setStyle(new ol.style.Style({
                    image:new ol.style.Icon({
                        src:'images/map_red_bg.png',
                        scale:1
                    }),
                    text:new ol.style.Text({
                        text:''+point.id,
                        fill:new ol.style.Fill({
                            color:'white'
                        }),
                        font:'20px sans-serif'
                    })
                })
        );
        source.addFeature(marker);
    }

    function clear(){
        var features=source.getFeatures();
        for(var i in features){
            source.removeFeature(features[i]);
        }
    }
    var coords=[];
    var feature;

    var style=new ol.style.Style({
        fill: new ol.style.Fill({
            color: 'rgba(96,96,96, 0.1)'
        }),
        stroke: new ol.style.Stroke({
            color: 'rgba(96,96,96, 0.5)',
            width: 2
        })

    })

    function addInteraction() {
        var value = 'Circle';
        var geometryFunction = ol.interaction.Draw.createBox();
        var draw = new ol.interaction.Draw({
            source: source,
            type: value,
            geometryFunction: geometryFunction
        });
        draw.on('drawend',function (evt) {
            try{
                if(feature){
                    source.removeFeature(feature);
                }
            }catch (e) {

            }

            feature=evt.feature;
            feature.setStyle(style);
            var poly=feature.getGeometry();
            coords=poly.getCoordinates();
            $("#boxInfo").val(JSON.stringify(coords));
        })
        map.addInteraction(draw);
    }



</script>
</body>
</html>