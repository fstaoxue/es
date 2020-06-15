<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <base href="${req.contextPath }/">
    <link href="js/openlayer/ol.css" rel="stylesheet"/>
    <link href="layui/css/layui.css" rel="stylesheet"/>
    <link href="css/map.css" rel="stylesheet"/>
    <link href="css/bootstrap.min.css" rel="stylesheet" />
</head>
<body>

<div class="container">
    <div class="row">
        <h2>点击地图打点</h2>
        <textarea id="result" rows="10" cols="100"></textarea>
        <button type="button" class="btn btn-primary" id="exportMysql">导入点到mysql</button>
        <button type="button" class="btn btn-primary" id="exportEs">导入点到Es</button>
        <button type="button" class="btn btn-primary" id="refresh">刷新</button>
    </div>
    <div class="row">
        <div id="map"></div>
    </div>
    <div class="tooltip" id="overlay">
        单击绘制点
    </div>
</div>

<script src="js/jquery-1.9.1.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<script src="js/openlayer/ol.js" type="text/javascript"></script>
<script src="layui/layui.js" type="text/javascript"></script>
<script type="text/javascript">

    var layer;
    layui.use('layer', function(){
        layer = layui.layer;
    });

    var map;
    $(function () {
        init_map();
        init_event();
        init_btn_event();
    });
    var points=[];
    function init_btn_event(){
        $("#exportMysql").click(function(){
            $.ajax({
                url:"/es/es/exportMysql",
                type:'post',
                data:{
                    points:JSON.stringify(points)
                },
                dataType:'json',
                timeout:60000,
                success:function(data){
                    if(data&&data.code==200){
                        layer.msg("导入到mysql成功");
                    }else{
                        layer.msg("导入到mysql失败");
                    }
                },
                error:function(){
                    layer.msg("导入到mysql失败");
                }
            })
        });
        $("#exportEs").click(function(){
            $.ajax({
                url:'/es/es/exportEs',
                type:'post',
                data:{
                    points:JSON.stringify(points)
                },
                dataType:'json',
                timeout:60000,
                success:function(data){
                    if(data&&data.code==200){
                        layer.msg("导入到es成功");
                    }else{
                        layer.msg("导入到es失败");
                    }
                },
                error:function(){
                    layer.msg("导入到es失败");
                }
            })
        });
        $("#refresh").click(function () {
            markerCount=0;
            points.splice(0,points.length);
            $("#result").text("");
            var features=source.getFeatures();
            for(var i in features){
                source.removeFeature(features[i]);
            }
        });
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


    var markerCount=0;

    function drawMarker(coordinate){
        markerCount++;
        var marker = new ol.Feature({
            geometry:new ol.geom.Point(coordinate)
        });

        marker.setStyle(new ol.style.Style({
                    image:new ol.style.Icon({
                        src:'images/map_red_bg.png',
                        scale:1
                    }),
                    text:new ol.style.Text({
                        text:''+markerCount,
                        fill:new ol.style.Fill({
                            color:'white'
                        }),
                        font:'20px sans-serif'
                    })
                })
        );

        var point={};
        point.lat=coordinate[1];
        point.lng=coordinate[0];
        point.cp=Math.ceil(new Date().getTime()/1000);
        point.info="demo";
        points.push(point);
        source.addFeature(marker);
        var temp=$("#result").text();
        temp+=point.lat+","+point.lng+","+point.cp+"\n";
        $("#result").text(temp);
    }

    var infoWindow;
    function addOverlay(coord){
        infoWindow=new ol.Overlay({
            id:'efxt3',
            element:document.getElementById("overlay"),
            offset:[10,10],
            positioning:'top-center'
        });
        infoWindow.setPosition(coord);
        map.addOverlay(infoWindow);
        $("#overlay").show();
    }

    function init_event(){
        map.on("pointermove",function(evt){
            if(infoWindow){
                infoWindow.setPosition(evt.coordinate);
            }else{
                addOverlay(evt.coordinate);
            }
        });
        map.on("click",function(evt){
            drawMarker(evt.coordinate);
        })
    }

</script>
</body>
</html>