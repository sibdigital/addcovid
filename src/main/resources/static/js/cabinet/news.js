webix.i18n.setLocale("ru-RU");

webix.html.addStyle(".listStyle {float:left; margin:20px;} " +
    "a[href]{\n" +
    "\n" +
    "   cursor: pointer;\n" +
    "   outline: 0;" +
    "   }" +
    "a{\n" +
    "\n" +
    "   text-decoration: none;\n" +
    "   color: #222;" +
    "   }" +
    ".item{\n" +
    "\n" +
    "    padding:20px 0 25px;\n" +
    "    border-bottom: 1px solid #ddd;\n" +
    "  }" +
    ".item_wrap{\n" +
    "\n" +
    "    min-height: 50px;\n" +
    "    position: relative;\n" +
    "    padding: 0 25px;\n" +
    "  }" +
    ".l-col-center{\n" +
    "\n" +
    "    max-width: 680px;\n" +
    "    margin: 0 auto;\n" +
    "  }" +
    ".item_bottom{\n" +
    "\n" +
    "    position: absolute;\n" +
    "    bottom: -5px;\n" +
    "    left: 25px;\n" +
    "    right: 25px;\n" +
    "    font-size: 0;\n" +
    "    white-space: nowrap;\n" +
    "  }" +
    ".item_category{\n" +
    "\n" +
    "    font-size: 10px;\n" +
    "    line-height: 18px;\n" +
    "    color: #999;\n" +
    "    white-space: normal;\n" +
    "  }" +
    ".item_link{\n" +
    "\n" +
    "    display: block;\n" +
    "  }" +
    ".item_title{\n" +
    "\n" +
    "    font:bold 14px/16px GraphikCy-Semibold;\n" +
    "    display: block;\n" +
    // "    margin:10px 5px;\n" +
    // "    padding:10px;\n" +
    "  }" +
    ".custom_item{\n" +
    "\n" +
    // "    border:1px solid #DADEE0;\n" +
    "    white-space: normal;\n" +
    "    margin:10px 5px;\n" +
    "    margin:10px 5px;\n" +
    "    padding:10px;\n" +
    "  }" +
    ".old_custom_item{\n" +
    "\n" +
    // "    border:1px solid #DADEE0;\n" +
    "    margin:10px 5px;\n" +
    "    margin:10px 5px;\n" +
    "    padding:10px;\n" +
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

const newsArchive = {
    view: 'scrollview',
    scroll: 'xy',
    id: 'newsArchiveId',
    body: {
        rows: [
            {
                view: "toolbar",
                id: "newsToolbar",
                cols: [
                    {},
                    {
                        view: "button",
                        type: "icon",
                        icon: "mdi mdi-undo",
                        label: "Назад к новостям",
                        align: "right",
                        maxWidth: 200,
                        click: function () {
                            webix.ui(news, $$('newsArchiveId'));
                        },
                    },
                ]
            },
            {
                view: "dataview",
                id: "newsDataview",
                margin: 20, paddingX: 10,
                scroll: 'y',
                template: function (obj) {
                    var htmlcode = ""
                    if (Date.parse(obj.endTime) < Date.now())
                        htmlcode = htmlcode + "<div item_id='id' class='old_custom_item'>"
                    else
                        htmlcode = htmlcode + "<div item_id='id' class='custom_item'>"
                    htmlcode = htmlcode + "<h3 style=\"color: #2e6c80;\"><a href = \"news\\" + obj.id + "\">" + obj.heading + "</a></h3>" + obj.message
                    let startTime = new Date(Date.parse(obj.startTime))
                    let startTimeString = startTime.getDay() + "." + startTime.getMonth() + "." + startTime.getFullYear()
                    htmlcode = htmlcode + "<div style='text-align:right;'>Дата публикации: " + startTimeString + "</div></div>"

                    return htmlcode
                },
                xCount: 1,
                type: {
                    // Если height поставить auto,
                    // то скроллинг с динамической загрузкой новостей не будет работать
                    height: 200,
                    width: "auto",
                    float: "right"
                },
                datafetch: 10,
                url: 'idata->' + news_archive_url,
            },
        ],
    }
};

const news = {
    view: 'scrollview',
    scroll: 'xy',
    id: 'newsId',
    body: {
        rows: [
            {
                view: "toolbar",
                id: "newsToolbar",
                cols: [
                    {},
                    {
                        view: "button",
                        type: "icon",
                        icon: "mdi mdi-newspaper",
                        label: "Архив новостей",
                        align: "right",
                        maxWidth: 150,
                        click: function () {
                            webix.ui(newsArchive, $$('newsId'));
                        },
                    }
                ]
            },
            {
                view: "dataview",
                id: "newsDataview",
                margin: 20, paddingX: 10,
                scroll: 'y',
                template: function (obj) {
                    var heading = "<span class = 'item_title rm-cm-item-text'> <a href = \"news\\" + obj.id + "\" style='text-decoration: none; color: #000000'>" + obj.heading + "</a></span>"

                    // htmlcode = htmlcode + "<h3><a href = \"news\\" + obj.id + "\" style='text-decoration: none; color: #000000'>" + obj.heading + "</a></h3>" + obj.message
                    let startTime = new Date(Date.parse(obj.startTime))
                    let startTimeString = startTime.getDay() + "." + startTime.getMonth() + "." + startTime.getFullYear()
                    var publicationDate = "Дата публикации: " + startTimeString

                    var htmlcode =
                        "<div class='item'>" +
                            "<div class='item_wrap'>" +
                                "<div class='item_bottom'>" +
                                    "<span class='item_category'>" + publicationDate +
                                    "</span>" +
                                "</div>" +
                                "<a href = \"news\\" + obj.id + "\" class='item-link'>"+
                                    "<span class='item_title'>" + obj.heading+
                                    "</span>" +
                                "</a>" +
                            "</div>" +
                        "</div>"

                    return htmlcode
                },
                xCount: 1,
                type: {
                    height: "auto",
                    width: "auto",
                    float: "right"
                },
                url: newsfeed_url,
            },
        ],
    }
};
