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

function getNewsTemplate() {
    return {
        view:"list",
        type:{
            templateStart: function (obj) {
                if (Date.parse(obj.endTime) < Date.now())
                    return "<div item_id='id' class='old_custom_item'>"
                else
                    return "<div item_id='id' class='custom_item'>"
            },
            template:"#heading#<br><br> #message#",
            templateEnd:function (obj) {
                let startTime = new Date(Date.parse(obj.startTime))
                let startTimeString = startTime.getDay() + "." + startTime.getMonth() + "." + startTime.getFullYear()
                return "<div style='text-align:right;'>Дата публикации: " + startTimeString+ "</div></div>"
            }
        },
        url: 'newsfeed'
    };
}

const news = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                // header: "Новости",
                body: getNewsTemplate()
            },
        ],
    }
}
