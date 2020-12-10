webix.i18n.setLocale("ru-RU");

webix.html.addStyle(".listStyle {float:left; margin:20px;} " +
    ".custom_item{\n" +
    "\n" +
    "    border:2px solid #1CA1C1;\n" +
    "    white-space: normal;\n" +
    "    margin:10px 5px;\n" +
    "    margin:10px 5px;\n" +
    "    padding:10px;\n" +
    "  }" +
    ".old_custom_item{\n" +
    "\n" +
    "    border:1px solid #DADEE0;\n" +
    "    margin:10px 5px;\n" +
    "    margin:10px 5px;\n" +
    "    padding:10px;\n" +
    "  }");

var url = 'newsfeed'

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

const news = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view:"dataview",
                id: "newsDataview",
                scroll:'y',
                template:  function (obj) {
                    var htmlcode = ""
                    if (Date.parse(obj.endTime) < Date.now())
                        htmlcode = htmlcode + "<div item_id='id' class='old_custom_item'>"
                    else
                        htmlcode = htmlcode + "<div item_id='id' class='custom_item'>"
                    htmlcode = htmlcode +  "<h3 style=\"color: #2e6c80;\"><a href = \"news\\" + obj.id + "\">" + obj.heading + "</a></h3>" + obj.message
                    let startTime = new Date(Date.parse(obj.startTime))
                    let startTimeString = startTime.getDay() + "." + startTime.getMonth() + "." + startTime.getFullYear()
                    htmlcode = htmlcode + "<div style='text-align:right;'>Дата публикации: " + startTimeString+ "</div></div>"

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
                url: 'idata->' + url,
            },
        ],
    }
};
