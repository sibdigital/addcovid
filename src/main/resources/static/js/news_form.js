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
    ".item{\n" +
    "\n" +
    "    margin:10px 5px;\n" +
    "    padding:10px 10px;\n" +
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
var options = {
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
};
webix.ready(function() {
    webix.ui({
        view:'template',
        template: function (obj) {
            if (obj.hashId != null) {
                var startTime =obj.startTime ? new Date(obj.startTime) : "";
                let startTimeString = startTime.toLocaleString("ru", options)
                return "<div>" +
                    "<span class = 'item_big_title'>" +
                    "<a href = \"news?hash_id=" + obj.hashId + "\" style='text-decoration: none;'>" +
                    obj.heading +
                    "</a>" +
                    "</span>" +
                    "<div class='item'>" +
                    obj.message +
                    "</div>" +
                    "<span class = 'item_label'>" +
                    "Дата публикации: " + startTimeString +
                    "</span>" +
                    "</div>"
            }
            else {
                return ""
            }

        },
        url: '../news/' + HASH_ID,
    },)

})
