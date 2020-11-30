const settings = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'form_pass',
                complexData: true,
                elements: [
                    {
                        view: 'text',
                        id: 'new_pass',
                        name: 'password',
                        label: 'Новый пароль',
                        labelPosition: 'top',
                        type: 'password',
                        maxWidth: 300
                    },
                    {
                        view: 'text',
                        id: 'retry_pass',
                        label: 'Подтвердите новый пароль',
                        labelPosition: 'top',
                        type: 'password',
                        maxWidth: 300,
                        on: {
                            onChange(newVal, oldVal) {
                                if (newVal === $$('new_pass').getValue()) {
                                    $$('save_pass').enable();
                                } else {
                                    $$('save_pass').disable();
                                }
                            }
                        }
                    },
                    {
                        view: 'button',
                        id: 'save_pass',
                        css: 'webix_primary',
                        value: 'Применить',
                        click: () => {
                            webix.ajax().headers({'Content-Type': 'application/json'})
                                .post('/save_pass', $$('form_pass').getValues()).then(function (data) {
                                if (data.text() === 'Пароль обновлен') {
                                    $$('new_pass').setValue('');
                                    $$('retry_pass').setValue('');
                                    $$('save_pass').disable();
                                    webix.message(data.text(), 'success');
                                } else {
                                    webix.message(data.text(), 'error');
                                }
                            });
                        },
                        disabled: true
                    }
                ]
            }
        ]
    }
}
