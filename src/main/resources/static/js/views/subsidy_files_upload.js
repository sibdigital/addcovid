function subsidy_files_upload() {

    let a = {
        id: 'a',
        rows: [
            view_section('GLA'),
            {
                scroll: "auto",
                template:
                    '<div class="filepond-wrapper">' +
                        '<input type="file" class="filepond" name="filepond"/>' +
                    '</div>'
            },
            view_section('PDR'),
            {
                scroll: "auto",
                template:
                    '<div class="filepond-wrapper">' +
                        '<input type="file" class="filepond" name="filepond"/>' +
                    '</div>'
            },
            view_section('XDF'),
            {
                scroll: "auto",
                template:
                    '<div class="filepond-wrapper">' +
                        '<input type="file" class="filepond" name="filepond"/>' +
                    '</div>'
            }
        ]
    }
    setTimeout(() => {
        // console.log(document.querySelector('input[name=filepond]'))
        let a = FilePond.parse(document.body);
        // console.log(a[0].getFiles());
        FilePond.setOptions({
            instantUpload: true,
            allowMultiple: true,
            allowProcess: true,
            allowRevert: true,
            allowReorder: true,
            server: {
                process: {
                    url: 'upload_subsidy_files',
                    method: 'POST',
                    withCredentials: false,
                    onload: () => {
                        alert(1);
                    }
                },
                revert: 'delete_subsidy_files'
            }
        })
    })
    return a;
}
