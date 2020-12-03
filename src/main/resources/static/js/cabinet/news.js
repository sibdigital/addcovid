const news = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'form',
                complexData: true,
                elements: [
                    {
                        view: 'list',
                        url: 'newsfeed',
                    }
                ],
                //url: 'documents'
            }
        ],
    }
}
