define(function () {
    return filter = {
        searchBar: function (tablename) {
            return {
                view: 'search',
                id: 'search',
                maxWidth: 300,
                minWidth: 100,
                placeholder: "Поиск по ИНН и названию",
                on: {
                    // onTimedKeyPress: function () {
                    //     let text = this.getValue().toLowerCase();
                    //     let table = $$(tablename);
                    //     if (!text) {
                    //         table.filter()
                    //     }
                    //     else {
                    //         let columns = table.config.columns
                    //         table.filter(function (obj) {
                    //             let flag = 0
                    //             if(obj.organization.inn.indexOf(text) !== -1) flag += 1
                    //             if(obj.organization.name.toUpperCase().indexOf(text.toUpperCase()) !== -1) flag += 1
                    //             return flag > 0 ? true : false
                    //         })
                    //     }
                    // }

                    onTimedKeyPress: function () {
                        let text = this.getValue().toLowerCase();
                        let table = $$(tablename);
                        if (!text) {
                            table.filter()
                        }
                        else {
                            //url = 'list_requestByInnAndName/' + ID_DEPARTMENT + '/' + status + '/' + text
                            oldUrl = table.config.url
                            filterUrl = oldUrl + '/' +  text
                            table.clearAll();
                            table.load (filterUrl)
                           // table.config.url =
                        }
                    }


                }
            }
        }
    }
})