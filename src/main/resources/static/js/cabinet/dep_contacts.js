const depContacts = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'datatable',
                id: "dep_contacts_table",
                // select: "row",
                // navigation: true,
                // resizeColumn: true,
                fixedRowHeight:false,
                columns: [
                    {
                        header: "№",
                        id: 'index',
                        name: 'index',
                        readonly: true,
                    },
                    {
                        header: "ИОГВ",
                        template: function (obj) {
                            if (obj.department.fullName != null) {
                                return "<p>" + obj.department.fullName + "</p>"
                            }
                            else {
                                return "<p>" + obj.department.name + "</p>"
                            }
                        },
                        adjust: true,
                        fillspace: true,
                        readonly: true,
                    },
                    {
                        header: "Телефоны",
                        template: function (obj) {
                            temp = "";
                            if (obj.phones.length != 0) {
                                for (var i in obj.phones) {
                                    var contact = obj.phones[i];
                                    if (contact.description == '') {
                                        temp = temp + "<p>" + contact.contactValue + "</p>";
                                    } else {
                                        temp = temp + "<p>" + contact.description + ": " + contact.contactValue + "</p>";
                                    }
                                }
                            }
                            return temp;
                        },
                        fillspace: true,
                        // adjust: true,
                        readonly: true,
                    },
                    {
                        header: "Электронная почта",
                        template: function (obj) {
                            temp = "";
                            if (obj.emails.length != 0) {
                                for (var i in obj.emails) {
                                    var contact = obj.emails[i];
                                    if (contact.description == '') {
                                        temp = temp + "<p>" + contact.contactValue + "</p>";
                                    } else {
                                        temp = temp + "<p>" + contact.description + ": " + contact.contactValue + "</p>";
                                    }
                                }
                            }
                            return temp;
                        },
                        fillspace: true,
                        // adjust: true,
                        readonly: true,
                    },
                ],
                on: {
                    'data->onStoreUpdated': function() {
                        this.data.each(function (obj, i) {
                            obj.index = i + 1;
                        });
                        this.adjustRowHeight(null, true);
                    }
                },
                url: 'dep_contacts'
            },
        ],
    }
}