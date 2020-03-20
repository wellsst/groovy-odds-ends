// Code goes here

var dtColumns = [{
    data: 'Name',
    title: 'NAME',
    width: '100'
}, {
    data: 'Position',
    title: 'POSITION',
    width: '100'
}, {
    data: 'Office',
    title: 'OFFICE',
    width: '100'
}, {
    data: 'Age',
    title: 'AGE',
    width: '100'
}, {
    data: 'Start date',
    title: 'START DATE',
    width: '100'
}, {
    data: 'Salary',
    title: 'SALARY',
    width: '100'
}];

$(document).ready(function() {
    $('#example').DataTable({
        dom: 'Bfrtip',
        // dom: 'C<"clear">lfrtip',
        paging: false,
        stateSave: true,
        // autoWidth: false,
        searching: true,
        scrollY: 400,
        scrollX: true,
        columns: dtColumns,
        // colVis: {
        //         restore: "Restore",
        //         showAll: "Show all",
        //         showNone: "Show none"
        // },
        buttons: [
            {
                extend: 'colvis',
                postfixButtons: [ 'colvisRestore' ]
            }
        ],
        info: false
    });
});