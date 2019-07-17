importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request","miniui-tools", "search-box","message"],function (request,tools,SearchBox,message) {
        new SearchBox({
            container: $("#search-box"),
            onSearch: search,
            initSize:2
        }).init();
        tools.bindOnEnter("#search-box", search);

        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);
        grid.setUrl(request.basePath + "message-server/problem-report");
        function search() {
            tools.searchGrid("#search-box", grid);
        }
        search();

        //状态
        var statusCombo = mini.getByName("status");
        var statusData = [];
        statusData.push({"text": "未回复", "value": "1"});
        statusData.push({"text": "已回复", "value": "2"});
        statusCombo.setData(statusData);
        statusCombo.on("valuechanged", function () {
            search();
        });

        //获取枚举值中定义的类型值
        var problemTypeData = mini.get("problemType");
        request.get("message-server/dictionary/define/ProblemTypeEnum/items", function (response) {
            problemTypeData.setData(response.result);
        });
        problemTypeData.on("valuechanged", function () {
            search();
        });
        window.createTime = function(e){
            var val = "";
            if(e.value != undefined || e.value != null){
                val = e.value;
            }else{
                val = e;
            }
            var responseDate = new Date(val);
            return mini.formatDate(responseDate, "yyyy-MM-dd HH:mm:ss");
        };

        window.problemStatus = function(e){
            var name = '';
            var val = "";
            if(e.value != undefined || e.value != null){
                val = e.value;
            }else{
                val = e;
            }
            if (val === 1 || val === "1") {
                name = '未回复';
            } else if(val === 2 || val === "2") {
                name = '已回复';
            }else{
                name = '其他';
            }
            return name;
        };


        $(".cancel-button").on("click", function () {
            var versionForm = mini.get("update-info-form");
            versionForm.showAtPos('center', 'middle');
        });

        window.renderAction = function (e) {
            var row = e.record;
            var status = row.status;
            var html = [];

            var detailBtn = tools.createActionButton("详情", "icon-find", function () {
                var versionForm = mini.get("info-form");
                versionForm.showAtPos('center', 'middle');
                var content = $("#content");
                $(".info-key").siblings().text("");
                $(".handleContent").html("");
                $(".feedbackImg1").html("");
                request.get("message-server/problem-report/" + row.id, function (e) {
                    var data = e.result;
                    for (var key in data) {
                        var value = data[key];
                        if(key === "fileList"){
                            var imgHtml = [];
                            var imgValue= [];
                            value.forEach(function (value) {
                                value=value.replace(/[\ [|\]|\"]/g,"");
                                imgValue=value.split(",");
                                if(imgValue.length>0) {
                                    for(var i=0;i<imgValue.length;i++){
                                        imgHtml.push('<a href="' + imgValue[i] + '" target="_blank"><img src="' + imgValue[i] + '" class="img-key"></a>');
                                    }
                                }
                            });
                            $(".feedbackImg1").append(imgHtml.join(""));
                        }
                        if(key === "createTime"){
                            content.find("." + key).text(window.createTime(value) || "");
                        }else if(key === "status"){
                            content.find("." + key).text(window.problemStatus(value) || "");
                        }else if(key === "problemType"){
                            content.find("." + key).text(value.text || "");
                        }else if(key === "handleTime"){
                            content.find("." + key).text(window.createTime(value) || "");
                        }else{
                            content.find("." + key).text(value || "");
                        }
                    }
                });
            });

            var editBtn = tools.createActionButton("回复", "icon-edit", function () {
                var versionForm = mini.get("update-info-form");
                versionForm.showAtPos('center', 'middle');
                var content = $("#update-content");
                $(".info-key").siblings().text("");
                $('textarea[name="handleContent"]').val("");
                $(".save-button").show();
                $(".feedbackImg2").html("");
                request.get("message-server/problem-report/" + row.id, function (e) {
                    var data = e.result;
                    for (var key in data) {
                        var value = data[key];
                        if(key === "fileList"){
                            var imgHtml = [];
                            var imgValue= [];
                            value.forEach(function (value) {
                                value=value.replace(/[\ [|\]|\"]/g,"");
                                imgValue=value.split(",");
                                if(imgValue.length>0) {
                                    for(var i=0;i<imgValue.length;i++){
                                        imgHtml.push('<a href="' + imgValue[i] + '" target="_blank"><img src="' + imgValue[i] + '" class="img-key"></a>');
                                    }
                                }
                            });
                            $(".feedbackImg2").append(imgHtml.join(""));
                        }
                        if(key === "createTime"){
                            content.find("." + key).text(window.createTime(value) || "");
                        }else if(key === "status"){
                            content.find("." + key).text(window.problemStatus(value) || "");
                        }else if(key === "problemType"){
                            content.find("." + key).text(value.text || "");
                        }else{
                            content.find("." + key).text(value || "");
                        }
                    }
                });
                $(".save-button").unbind("click").on("click", function () {
                    require(["message"], function (message) {
                        var handleContent = $('textarea[name="handleContent"]').val();
                        if(handleContent.length < 1){
                            mini.alert("请输入回复内容……");
                            return false;
                        }
                        if(handleContent.length > 200){
                            mini.alert("回复内容请控制在200个字符以内");
                            return false;
                        }
                        var loading = message.loading("提交中...");
                        request.patch("message-server/problem-report/update-problem-report", {
                            id: row.id,
                            handleContent: handleContent
                        }, function (res) {
                            loading.close();
                            if (res.status === 200){
                                $(".save-button").hide();
                                grid.reload();
                            } else {
                                message.showTips("回复失败"+res.message);
                            }
                        });
                    });
                });
            });
            html.push(detailBtn);
            if (status === 1) {
                if (authorize.hasPermission("problem-report", "reply")) {
                    html.push(editBtn);
                }
            }
            return html.join("");
        }
    });

});