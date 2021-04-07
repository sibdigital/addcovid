const dateFormatWOTime = webix.Date.dateToStr("%d.%m.%Y")
const xml_format =  webix.Date.strToDate("%Y-%m-%d %H:%i:%s.S");

function changeContentView(newView) {
    webix.ui({
        id: 'content',
        rows: [
            newView
        ]
    }, $$('content'));
}

