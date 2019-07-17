importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "search-box"], function (request, tools, SearchBox) {

        $("#submit").on("click",function () {
            var form = new mini.Form("#form1");
            var data = form.getData();      //获取表单多个控件的数据
            console.log(data)
            request.post("interaction-server/device/command/test",JSON.stringify(data), function (e) {
                console.log(e);
            });
        });
    });

});