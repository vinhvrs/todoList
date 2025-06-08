document.addEventListener('DOMContentLoaded', function() {
    const APIurl = "http://127.0.0.1:1111/api/getUser";
    const tableContent = document.getElementById('tableContent');

    fetch(APIurl)
        .then(response => response.json())
        .then(data => {
            data.forEach(users => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td> ${users.id} </td>
                    <td> ${users.username} </td>
                    <td> ${users.email} </td>
                    <td> ${users.listid} </td>
                `;
                if (row === null) {
                    console.log('No data found');
                } else {
                    console.log('Data found');
                    tableContent.appendChild(row);
                }
            });
        })
        .catch(error => console.log('Error:', error)
    );

});