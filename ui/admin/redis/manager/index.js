importResource("/admin/css/common.css");

importMiniui(function () {

    mini.parse();
    var clientsTree = mini.get("clientsTree");
    var keysGrid = mini.get("keysGrid");
    var mainTabs = mini.get("mainTabs");

    require(["request", "miniui-tools", "message"], function (request, tools, message) {

        function renderIcon(e) {
            var node = e.node;
            if (node._type === 'client') {
                e.iconCls = "fa fa-table";
                e.isLeaf = false;
            } else if (node._type === 'database') {
                e.iconCls = "fa fa-database";
                e.isLeaf = false;
            } else {
                e.iconCls = "fa fa-columns";
            }
        }

        function addMainTab(title, html,node) {
            var tab = mainTabs.getTab(title) || mainTabs.addTab({name: title, title: title, showCloseButton: true});
            //tab body
            var el = mainTabs.getTabBodyEl(tab);
            var top = " <div>\n" +
                "            <span class=\"font key-type\"></span>\n" +
                "            <input style=\"width:60%\" name=\"key\" class=\"mini-textbox\"/>\n" +
                "            <span class=\"font\">TTL:</span><span class=\"ttl font\"></span>\n" +
                "            <a class=\"mini-button config\" plain=\"true\">配置</a>\n" +
                "            <a class=\"mini-button remove\" iconCls=\"icon-remove\" plain=\"true\">删除</a>\n" +
                "        </div>";
            el.innerHTML = top + html;
            var newHTML= $(el);
            newHTML.find(".key-type").text(node.type);
            newHTML.find(".ttl").text(node.ttl);
            newHTML.find("input[name=key]").val(node.key);


            //active tab
            mainTabs.activeTab(tab);
            return newHTML;
        }

        var counter = 0;

        function initCommonsEvent(node, html) {
            html.find(".remove").on("click", function () {
                message.confirm("确认删除?", function () {
                    request['delete']("redis/manager/" + node.clientId + "/" + node.database + "/keys/" + node.key, function (resp) {
                        if (resp.status === 200) {
                            clientsTree.removeNode(node);
                            mainTabs.removeTab(mainTabs.getActiveTab())
                        }
                    });
                })
            })
        }

        var init = {
            string:function (node) {
                var key = node.key;
                var newHTML = $(addMainTab(key,$("#template-string").html(),node));
                mini.parse();
                initCommonsEvent(node, newHTML);
                request.get("redis/manager/" + node.clientId + "/" + node.database + "/get/" + key, function (resp) {
                    if (resp.status === 200) {
                        var val = resp.result;

                        newHTML.find("[name=value]").val(val);
                    }
                });
            },
            hash: function (node) {
                var key = node.key;
                var gridId = 'grid_' + (counter++);
                var hashKeyId = "hash_key_" + (counter++);

                var newHTML = $(addMainTab(key, $("#template-hash").html(),node));

                newHTML.find("[name=hash-grid]").attr("id", gridId);
                newHTML.find("[name=hash-key]").attr("id", hashKeyId);


                mini.parse();
                initCommonsEvent(node, newHTML);
                var grid = mini.get(gridId);
                grid.getColumn("action").renderer = function (e) {
                    var row = e.record;
                    return tools.createActionButton("删除", "icon-remove", function () {
                        message.confirm("确认删除?", function () {
                            request['delete']("redis/manager/" + node.clientId + "/" + node.database + "/hdel/" + key + "/" + row.key, function (resp) {
                                if (resp.status === 200) {
                                    grid.removeRow(row);
                                }
                            });
                        })
                    })
                };

                request.get("redis/manager/" + node.clientId + "/" + node.database + "/hkeys/" + key, function (resp) {
                    if (resp.status === 200) {
                        var list = [];
                        $(resp.result).each(function () {
                            list.push({key: this, clientId: node.clientId, database: node.database})
                        });
                        grid.setData(list);
                    }
                });
                grid.on("rowclick", function (e) {
                    var row = e.record;
                    mini.get(hashKeyId).setValue(row.key);
                    request.get("redis/manager/" + node.clientId + "/" + node.database + "/hget/" + key + "/" + row.key, function (resp) {
                        if (resp.status === 200) {
                            var val = resp.result;

                            newHTML.find("[name=hvalue]").val(val);
                        }
                    });
                    console.log(e);
                })

            }
        }

        clientsTree.on("nodeclick", function (e) {
            var node = e.node;
            if (node._type === 'key') {
                if (init[node.type]) {
                    init[node.type](node);
                }
            }
            console.log(node);
        });

        clientsTree.on("drawnode", renderIcon);

        clientsTree.on("beforeexpand", function (e) {
            var node = e.node;
            if (node._type === 'client' && !node.loaded) {
                e.cancel = true;
                request.get("redis/manager/" + node.id + "/databases", function (resp) {
                    node.loaded = true;
                    if (resp.status === 200) {
                        var databases = [];
                        for (var i = 0; i < resp.result; i++) {
                            databases.push({
                                clientId: node.id,
                                _type: "database",
                                database: i,
                                expanded: false,
                                name: "db" + i
                            });
                        }
                        // node.children=table;
                        node.children = [];
                        clientsTree.addNodes(databases, node);
                        clientsTree.expandNode(node);
                    }
                })
            }
            if (node._type === 'database' && !node.loaded) {
                e.cancel = true;
                request.get("redis/manager/" + node.clientId + "/" + node.database + "/keys", function (resp) {
                    node.loaded = true;
                    if (resp.status === 200) {
                        $(resp.result).each(function () {
                            this._type = "key";
                            this.name = this.key + "(" + this.type + ")";
                            this.clientId = node.clientId;
                            this.database = node.database;
                        });
                        // node.children=table;
                        node.children = [];
                        clientsTree.addNodes(resp.result, node);
                        clientsTree.expandNode(node);
                    } else {
                        node.loaded = false;
                    }
                })
            }
        });

        function loadRedisClient() {
            request.get("redis/manager/clients", function (response) {
                    if (response.status === 200) {
                        var clients = response.result;
                        $(clients).each(function () {
                            this._type = 'client';
                        });
                        clientsTree.loadList(clients);
                    }
                }
            );
        }

        loadRedisClient();
    })

});
