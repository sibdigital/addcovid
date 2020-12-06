webix.html.addStyle(".listStyle {float:left; margin:20px;} " +
    ".custom_item{\n" +
    "\n" +
    "    border:1px solid #1CA1C1;\n" +
    "    border-radius:10px;\n" +
    "    margin:10px 5px;\n" +
    "    margin:10px 5px;\n" +
    "    padding:10px;\n" +
    "  }");


function getNewsTemplate() {
    return {
        view:"list",
        type:{
            templateStart:"<div item_id='id' class='custom_item'>",
            template:"#heading#<br><br> #message#",
            templateEnd:"<div style='text-align:right;'>Дата публикации: #startTime#</div></div>"
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
            }
        ],
    }
}
