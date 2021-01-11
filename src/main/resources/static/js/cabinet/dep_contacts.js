const depContacts = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'datatable',
                id: "dep_contacts_table",
                select: "row",
                navigation: true,
                fixedRowHeight:false,
                columns: [
                    {
                        header: "№",
                        id: 'index',
                        width: 70,
                        name: 'index',
                        readonly: true,
                    },
                    {
                        header: "ИОГВ",
                        template: obj => getDepartmentName(obj),
                        minWidth: 150,
                        adjust: true,
                        fillspace: true,
                        readonly: true,
                    },
                    {
                        header: "Телефоны",
                        template: obj => getPhones(obj),
                        minWidth: 150,
                        fillspace: true,
                        // adjust: true,
                        readonly: true,
                    },
                    {
                        header: "Электронная почта",
                        template: obj => getEmails(obj),
                        fillspace: true,
                        // adjust: true,
                        minWidth: 150,
                        readonly: true,
                    },
                ],
                on: {
                    'data->onStoreUpdated': function() {
                        this.data.each(function (obj, i) {
                            var j = i + 1;
                            obj.index = "<p>" + j + "</p>";
                        });
                        this.adjustRowHeight(null, true);
                    }
                },
                url: 'dep_contacts'
            },
        ],
    }
}


function getDepartmentName(obj) {
    if (obj.department.fullName != null) {
        return "<p>" + obj.department.fullName + "</p>"
    }
    else {
        return "<p>" + obj.department.name + "</p>"
    }
}

function getPhones(obj) {
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
}

function getEmails(obj) {
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
}
