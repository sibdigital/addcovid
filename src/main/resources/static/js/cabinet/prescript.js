const prescript = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'datatable',
                id: "prescriptions_table",
                minWidth: 220,
                select: "row",
                navigation: true,
                resizeColumn: true,
                datafetch: 25,
                columns: [
                    {header: "Наименование предписания", template: "#activityKind#", minWidth: 130, fillspace: true},
                    {id: 'time_Publication', header: "Дата публикации", adjust: true, format: dateFormat},
                ],
                scheme: {
                    $init: function (obj) {
                        if (obj.statusPublication == 1) {
                            obj.time_Publication = obj.timePublication ? obj.timePublication.replace("T", " ") : "";
                        }
                    },
                },
                on: {
                    onBeforeLoad: function () {
                        this.showOverlay("Загружаю...");
                    },
                    onAfterLoad: function () {
                        this.hideOverlay();
                        if (!this.count()) {
                            this.showOverlay("Отсутствуют данные")
                        }
                    },
                    onLoadError: function () {
                        this.hideOverlay();
                    },
                    onItemClick: function (id) {
                        showRequestCreateForm(id, 3)
                    },
                },
                url: 'prescriptions'
            }
        ]
    }
}
