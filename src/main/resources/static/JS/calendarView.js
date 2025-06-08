const calendarBody = document.getElementById('calendarBody');
const monthYear = document.getElementById('monthYear');
const prev = document.getElementById('prev');
const next = document.getElementById('next');
const detailPanel = document.getElementById('detailPanel');
const closePanel = document.getElementById('closePanel');
const selectedDateText = document.getElementById('selectedDate');
const taskList = document.getElementById('taskList');
const userID = localStorage.getItem('userID');

let currentDate = new Date();
let selectedCell = null;

function renderCalendar(date) {
    calendarBody.innerHTML = '';
    const year = date.getFullYear();
    const month = date.getMonth();
    monthYear.textContent = date.toLocaleString('default', { month: 'long', year: 'numeric' });

    const firstDayIndex = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    let dateCount = 1;

    for (let i = 0; i < 6; i++) {
        const row = document.createElement('tr');
        for (let j = 0; j < 7; j++) {
            const cell = document.createElement('td');
            if ((i === 0 && j < firstDayIndex) || dateCount > daysInMonth) {
                cell.textContent = '';
            } else {
                const day = dateCount;
                cell.textContent = day;
                cell.dataset.date = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

                cell.addEventListener('click', function () {
                    if (selectedCell) selectedCell.classList.remove('selected');
                    cell.classList.add('selected');
                    selectedCell = cell;
                    selectedDateText.textContent = cell.dataset.date;
                    openDetailPanel(cell.dataset.date);
                });

                dateCount++;
            }
            row.appendChild(cell);
        }
        calendarBody.appendChild(row);
    }
    updateCalendarColors();
}

function createNewListbyDate(date) {
    const newList = {
        title: date,
        description: "This is a new list created on " + date,
        userID: userID,
        dueDate: date,
        status: "Incomplete"
    };

    fetch(`http://127.0.0.1:1111/api/${userID}/addList`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(newList)
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error("Failed to create new list");
        }
    })
    // .then(data => {
    //     //console.log("New list created:", data);
    // })
    .catch(error => {
        console.error("Error creating new list:", error);
    });
}

async function updateCalendarColors() {
    const cells = document.querySelectorAll("#calendarBody td");

    for (const cell of cells) {
        if (!cell.dataset.date) continue;

        const date = cell.dataset.date;
        let allCompleted = true;
        let hasUncompleted = false;
        let hasOverdue = false;

        try {
            let response = await fetch(`http://127.0.0.1:1111/api/${userID}/getCalendarItems/${date}`,{
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                }
            });
            let data = json = [];
            if (response.ok){
                data = await response.json();
            } else {
                console.error(`Failed to fetch tasks for ${date}:`, response.statusText);
                continue;
            }
            
            //if (data.length === 1 && data[0].title === 'No tasks found.') continue;
            if (data.length === 0) {
                cell.classList.remove("bg-success", "bg-danger", "bg-secondary");
                continue; // No tasks for this date, skip color update
            }

            data.forEach(task => {
                if (task.status !== "Completed") {
                    hasUncompleted = true;
                    allCompleted = false;
                }

                if (new Date(task.dueDate) < new Date()) {
                    hasOverdue = true;
                    allCompleted = false;
                }
            });

            cell.classList.remove("bg-success", "bg-danger", "bg-secondary");

            if (hasOverdue) {
                cell.classList.add("bg-secondary");
            } else if (allCompleted) {
                cell.classList.add("bg-success");
            } else if (hasUncompleted) {
                cell.classList.add("bg-danger");
            }
        } catch (error) {
            console.error(`Error fetching tasks for ${date}:`, error);
        }
    }
}

function openDetailPanel(date) {
    closeDetailPanel();
    detailPanel.classList.add('open');
    fetchTasks(date);
}

function closeDetailPanel() {
    detailPanel.classList.remove('open');
}

async function fetchTasks(date) {
    taskList.innerHTML = '';
    try {
        const response = await fetch(`http://127.0.0.1:1111/api/${userID}/getCalendarItems/${date}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                }
            }
        );
        const data = await response.json();

        if (data.length === 1 && data[0].title === 'No tasks found.') {
            taskList.innerHTML = '<p class="text-muted">No tasks found.</p>';
            return;
        }

        data.forEach(task => {
            const taskElement = document.createElement('div');
            taskElement.classList.add('card', 'mb-2', 'shadow-sm', 'border');

            taskElement.innerHTML = `
                <div class="card-body">
                    <h5 class="card-title">${task.title}</h5>
                    <p class="card-text">${task.description || "No description available"}</p>
                    <div class="d-flex justify-content-end">
                        <button class="btn btn-success btn-sm me-2" onclick="markDone('${task.id}', '${task.listID}')">✔ Done</button>
                        <button class="btn btn-danger btn-sm" onclick="removeTask('${task.id}', '${task.listID}')">🗑 Remove</button>
                    </div>
                </div>
            `;

            taskList.appendChild(taskElement);
        });

    } catch (error) {
        console.error('Error fetching tasks:', error);
        taskList.innerHTML = '<p class="text-danger">Error fetching tasks.</p>';
    }
}

async function markDone(itemID, listID) {
    try {
        const response = await fetch(`http://127.0.0.1:1111/api/${userID}/getListItem/${listID}/${itemID}`);
        const existingTask = await response.json();
        existingTask.status = "Completed";

        await fetch(`http://127.0.0.1:1111/api/${userID}/updateListItem/${listID}/${itemID}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(existingTask)
        });

        updateCalendarColors();
        fetchTasks(selectedDateText.textContent);
    } catch (error) {
        console.error("Error marking task as done:", error);
    }
}

async function removeTask(itemID, listID) {
    if (!confirm("Are you sure you want to delete this task?")) return;

    try {
        await fetch(`http://127.0.0.1:1111/api/${userID}/removeTask/${listID}/${itemID}`, {
            method: "DELETE",
            headers: { "Content-Type": "application/json" }
        });

        updateCalendarColors();
        fetchTasks(selectedDateText.textContent);
    } catch (error) {
        console.error("Error removing task:", error);
    }
}

prev.addEventListener('click', () => { currentDate.setMonth(currentDate.getMonth() - 1); renderCalendar(currentDate); });
next.addEventListener('click', () => { currentDate.setMonth(currentDate.getMonth() + 1); renderCalendar(currentDate); });
closePanel.addEventListener('click', closeDetailPanel);

renderCalendar(currentDate);

async function findUnlist(date) {
    try {
        const response = await fetch(`http://127.0.0.1:1111/api/${userID}/getList`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            }
        });
        const data = await response.json();

        for (const list of data) {
            if (list.title === date) {
                return list.listID; // Correctly return the listID
            }
        }

        return null; // Return null if "Unlisted" is not found
    } catch (error) {
        console.error("Error fetching list:", error);
        return null;
    }
}

async function addInstanly() {
    listID = await findUnlist(selectedDateText.textContent);
    if (!listID) {
        const today = new Date().toISOString().split('T')[0];
        createNewListbyDate(today);
        listID = await findUnlist(today); // Retry to get the newly created listID
    }
    const detailPanel = document.getElementById("detailPanel");
    const selectedDate = document.getElementById("selectedDate");
    if ($(".todo-add-form").length > 0) return;

    const formHTML = `
      <div class="todo-add-form mt-2 p-2 border rounded">
        <input type="text" class="form-control mb-2" placeholder="To-Do Title" />
        <textarea class="form-control mb-2" placeholder="To-Do Description"></textarea>
        <input type="date" class="form-control mb-2" />
        <input type="time" step="1" class="form-control mb-2" />
        <div class="d-flex justify-content-end">
          <button class="btn btn-success me-2" onclick="addTodo('${listID}')">+</button>
          <button class="btn btn-danger" onclick="cancelInstanly('${listID}')">Cancel</button>
        </div>
      </div>
    `;

    detailPanel.innerHTML = detailPanel.innerHTML + formHTML;
}

function cancelInstanly(listID) {
    const detailPanel = document.getElementById("detailPanel");
    detailPanel.removeChild(detailPanel.lastChild);
    detailPanel.removeChild(detailPanel.lastChild);
}

