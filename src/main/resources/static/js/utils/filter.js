define(function () {
    return filter = {
        searchBar: function (tablename) {
            return {
                view: 'search',
                maxWidth: 300,
                minWidth: 100,
                placeholder: "Поиск...",
                on: {
                    onTimedKeyPress: function () {
                        let text = this.getValue().toLowerCase();
                        let table = $$(tablename);
                        if (!text) {
                            table.filter()
                        }
                        else {
                            let columns = table.config.columns
                            table.filter(function (obj) {
                                let flag = 0
                                for (key in obj) {
                                    let cell_data = '';
                                    if (obj[key] != null) {
                                        if (typeof(obj[key]) == 'object') {
                                            if (Array.isArray(obj[key])) cell_data = obj[key][0] ? obj[key][0].name || '' : ''
                                            else cell_data = obj[key].name || ''
                                        }
                                        else {
                                            cell_data = obj[key].toString()
                                        }
                                        //console.log(cell_data);
                                        if (cell_data.toLowerCase().indexOf(text) !== -1) flag += 1
                                    }
                                }
                                return flag > 0 ? true : false
                            })
                        }
                    }
                }
            }
        }
    }
})