<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>es demo</title>
    <link rel="stylesheet" href="layui/css/layui.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">es demo</div>
    </div>

    <div class="layui-side layui-bg-black">
        <div class="layui-side-scroll">
            <!-- 左侧导航区域（可配合layui已有的垂直导航） -->
            <ul class="layui-nav layui-nav-tree"  lay-filter="test">
                <li class="layui-nav-item layui-nav-itemed">
                    <a class="" href="javascript:;">es</a>
                    <dl class="layui-nav-child">
                        <dd class="gotoPage" name="upload"><a href="javascript:;">导入</a></dd>
                        <dd class="gotoPage" name="mysql"><a href="javascript:;">mysql列表展示</a></dd>
                        <dd class="gotoPage" name="box"><a href="javascript:;">es框选展示</a></dd>
                        <dd class="gotoPage" name="head"><a href="javascript:;">es head地址</a></dd>
                    </dl>
                </li>
            </ul>
        </div>
    </div>

    <div class="layui-body">
        <iframe src="" id="myframe" width="100%" height="100%"></iframe>
    </div>

    <div class="layui-footer">

    </div>
</div>
<script src="js/jquery-1.9.1.js"></script>
<script src="layui/layui.js"></script>
<script>
    //JavaScript代码区域
    layui.use('element', function(){
        var element = layui.element;

    });

    $(function(){
        init_router();
        $(".gotoPage").eq(0).click();
    })

    function init_router(){
        $(".gotoPage").click(function(){
            var name=$(this).attr("name");
            $("#myframe").attr("src","/es/"+name);
        })
    }
</script>
</body>
</html>