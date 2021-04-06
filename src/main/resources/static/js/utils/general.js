function changeContentView(newView) {
    webix.ui({
        id: 'content',
        rows: [
            newView
        ]
    }, $$('content'));
}