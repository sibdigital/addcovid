webix.i18n.setLocale("ru-RU");

webix.html.addStyle(".listStyle {float:left; margin:20px;} " +
    "a{\n" +
    "\n" +
    "   text-decoration: none;\n" +
    "   color: #000000;" +
    "   }" +
    "a:hover{\n" +
    "\n" +
    "   color: #0056b3; \n" +
    "   }" +
    "a.link{\n" +
    "\n" +
    "   text-decoration: none;\n" +
    "   color: #005cbf;" +
    "   }" +
    "a.link:hover{\n" +
    "\n" +
    "   color: #e83e8c; \n" +
    "   }" +
    ".item{\n" +
    "\n" +
    "    margin:10px 5px;\n" +
    "    padding:10px 10px;\n" +
    "  }" +
    ".item_link{\n" +
    "\n" +
    "    margin:0px 15px;\n" +
    "  }" +
    ".item_title{\n" +
    "\n" +
    "    font:bold 14px/16px GraphikCy-Semibold;\n" +
    "    display: block;\n" +
    "    margin:10px 5px;\n" +
    "    padding:10px;\n" +
    "  }" +
    ".item_big_title{\n" +
    "\n" +
    "    font:bold 24px/26px GraphikCy-Semibold;\n" +
    "    display: block;\n" +
    "    margin:10px 5px;\n" +
    "    padding:10px;\n" +
    "  }" +
    ".item_label{\n" +
    "\n" +
    "    font:bold 12px/14px GraphikCy-Semibold;\n" +
    "    text-align:right;\n" +
    "    display: block;\n" +
    "    margin:10px 5px;\n" +
    "    padding:10px;\n" +
    "  }" +
    ".class_border{\n" +
    "\n" +
    "    border:1px solid #c3c5c9;\n" +
    "  }");

var newsfeed_url = 'newsfeed'
var news_archive_url = 'news_archive'

//define proxy
webix.proxy.idata = {
    $proxy:true,
    load:function(view, params){
        this._attachHandlers(view);

        var url = this.source;
        url += (url.indexOf("?") == -1 ) ? "?": "&";

        var count = params?params.count:view.config.datafetch || 0;
        var start = params?params.start:0;

        //url will look like "../data.php?count=50&start=51"
        url += "count="+count;
        url += start?"&start="+start:"";

        return webix.ajax(url).then(webix.bind(function(data) {
            /*
                here the url outputs data in a classic format {data:[], pos:0, total_count:999}
                we take only data arry from it to emulate dynamic loading without knowing the total count
            */
            data = data.json().data;
            this._checkLoadNext(data);
            return data;
        }, this));
    },
    _checkLoadNext:function(data){
        if(!data.length)
            this._dontLoadNext = true;
    },
    _attachHandlers:function(view){
        var proxy = this;

        if(view.config.columns)
            view.attachEvent("onScrollY", function(){ proxy._loadNext(this); });
        else
            view.attachEvent("onAfterScroll", function(){ proxy._loadNext(this); });

        //attach handlers once
        this._attachHandlers = function(){};
    },
    _loadNext:function(view){
        var contentScroll =  view.getScrollState().y+view.$view.clientHeight;
        var node = view.getItemNode(view.getLastId());
        var height = view.config.rowHeight || view.type.height;

        if(node && contentScroll>=node.offsetTop+height && !this._dontLoadNext){
            view.loadNext(view.config.datafetch, view.count()+1);
        }
    }
};

var options = {
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
};

const news = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        id: 'contactsMainLayout',
        rows: [
            {
                type: 'wide',
                responsive: 'contactsMainLayout',
                cols: [
                    {
                        view: "dataview",
                        id: "newsDataview",
                        margin: 20, paddingX: 10,
                        scroll: 'y',
                        template: function (obj) {
                            let obj_news = obj['news']
                            let news_files = obj['newsFiles']
                            let startTime =obj_news.startTime ? new Date(obj_news.startTime) : "";
                            let startTimeString = startTime.toLocaleString("ru", options)

                            let htmlcode =  "<div class = 'class_border'>" +
                                    "<span class = 'item_big_title'>" +
                                            "<a href = \"news?hash_id=" + obj_news.hashId + "\"'>" +
                                                obj_news.heading +
                                            "</a>" +
                                            "</span>"+
                                            "<div class='item'>" +
                                                obj_news.message +
                                            "</div>";
                            for (var i in news_files) {
                                htmlcode = htmlcode + "<div class='item_link'><a class='link' href='" + news_files[i].attachmentPath + "'download=''><i class='mdi mdi-download'></i>"
                                            + news_files[i].originalFileName + "</a></div>"
                            }

                            return htmlcode +
                                            "<span class = 'item_label'>" +
                                            "Дата публикации: "+ startTimeString +
                                            "</span>" +
                                    "</div>"

                        },
                        xCount: 1,
                        type: {
                            height: "auto",
                            width: "auto",
                            float: "right"
                        },
                        url: newsfeed_url,
                    },
                    {   gravity: 0.3,
                        rows:
                        [
                            {
                                view: 'template',
                                type: 'section',
                                template: 'Архив новостей'
                            },
                            {
                                view: "dataview",
                                id: "newsDataview",
                                margin: 20, paddingX: 10,
                                scroll: 'y',
                                template: function (obj) {
                                    var startTime =obj.startTime ? new Date(obj.startTime) : "";
                                    let startTimeString = startTime.toLocaleString("ru", options)

                                    return "<span class = 'item_title'>" +
                                                "<a href = \"news?hash_id=" + obj.hashId + "\"'>" +
                                                    obj.heading +
                                                "</a>" +
                                            "</span>"+
                                            "<span class = 'item_label'>" +
                                                "Дата публикации: "+ startTimeString +
                                            "</span>"
                                },
                                xCount: 1,
                                type: {
                                    // Если height поставить auto,
                                    // то скроллинг с динамической загрузкой новостей не будет работать
                                    height: 100,
                                    width: "auto",
                                    float: "right"
                                },
                                datafetch: 10,
                                url: 'idata->' + news_archive_url,
                            },
                        ]
                    }

                ]
            }
        ]
    }
}