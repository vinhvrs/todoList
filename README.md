# TodoList - Spring Boot Fullstack App

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-template-yellow.svg)](https://www.thymeleaf.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![Build Passing](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Security](https://img.shields.io/badge/Spring%20Security-enabled-green)](https://spring.io/projects/spring-security)
[![REST API](https://img.shields.io/badge/REST%20API-JSON-red)]()
[![Docker Ready](https://img.shields.io/badge/Docker-ready-blue.svg)](https://www.docker.com/)

---

## 🚀 Overview

TodoList là nền tảng quản lý công việc **đa người dùng**, xây dựng bằng **Spring Boot** kết hợp MySQL, cung cấp cả giao diện động (Thymeleaf + Bootstrap) và RESTful API cho JS/frontend hiện đại.
**Chức năng chính:**

* Quản lý user (đăng ký, đăng nhập, đổi thông tin cá nhân, đổi mật khẩu)
* Quản lý list (nhiều list/project cho 1 user)
* Quản lý task (CRUD, trạng thái, deadline, calendar view)
* Tích hợp session-based authentication với Spring Security

---

## 🏗️ Project Structure & Main Components

* **`TodoListApplication.java`**: Khởi động app, cấu hình gốc Spring Boot
* **`config/SecurityConfig.java`**: Thiết lập security (route permitAll, CSRF, custom login page)
* **Controllers:**

  * **`AuthController.java`**: Xử lý API login, register, check session (REST/JSON)
  * **`APIController.java`**: REST API cho các thao tác todo (CRUD, calendar item...)
  * **`PageController.java`**: Render các trang Thymeleaf (index, login, register, calendar...)
  * **`TodoListController.java`**: Thao tác nâng cao với danh sách công việc
* **Model/Entities:**

  * **`User.java`**: Entity user, lưu thông tin account
  * **`TodoList.java`**: Entity danh sách project
  * **`TodoItems.java`**: Entity từng task, trạng thái, deadline
* **Repositories:**

  * `UserRepo.java`, `TodoRepo.java`, `ItemsRepo.java`: Spring Data JPA thao tác DB
* **Frontend:**

  * `/static/css/`, `/static/js/`: CSS/JS chia chức năng (login, register, calendar, ...)
  * `/templates/`: Giao diện Thymeleaf từng màn hình (login, register, calendar, changePassword...)

---

## ⚙️ Features

* Đăng ký, đăng nhập, đổi thông tin tài khoản (Spring Security session)
* Tạo/xóa/sửa list công việc (multi-project cho 1 user)
* CRUD từng task trong list (giao diện & API)
* Calendar view: xem công việc theo ngày, tự động tô màu các ngày có task completed/overdue
* Bảo vệ các route nhạy cảm, chỉ cho phép public login/register & static
* REST API cho JS/frontend (dùng fetch hoặc client riêng)

---

## ⚡ Tech Stack

| Layer       | Technology                         |
| ----------- | ---------------------------------- |
| Backend     | Java 21, Spring Boot 3.4.x         |
| Security    | Spring Security, session-based     |
| Persistence | Spring Data JPA, MySQL             |
| Frontend    | Thymeleaf, Bootstrap 5, Vanilla JS |
| Build       | Maven                              |
| (Optional)  | Docker, Docker Compose             |

---

## 📂 Folder Structure

```
src/
└── main/
    ├── java/com/todoList/todoList/
    │   ├── API/
    │   │   ├── APIController.java
    │   │   ├── AuthController.java
    │   │   ├── PageController.java
    │   │   └── TodoListController.java
    │   ├── config/SecurityConfig.java
    │   ├── Model/
    │   │   ├── User.java
    │   │   ├── TodoList.java
    │   │   └── TodoItems.java
    │   └── repo/
    │       ├── UserRepo.java
    │       ├── TodoRepo.java
    │       └── ItemsRepo.java
    └── resources/
        ├── static/
        │   ├── css/
        │   └── js/
        └── templates/
```

---

## 🛠️ Quick Start

### 1. Clone & Build

```bash
git clone https://github.com/vinhvrs/todoList.git
cd todoList
mvn clean package -DskipTests
```

### 2. Configure MySQL

* Tạo database `todo_list` hoặc tên bạn muốn.
* Update file `src/main/resources/application.properties`:

  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name?useSSL=false&serverTimezone=UTC
  spring.datasource.username=your_user
  spring.datasource.password=your_pass
  spring.jpa.hibernate.ddl-auto=update
  server.port=1111
  ```

### 3. Run Application

```bash
java -jar target/todoList-0.0.1-SNAPSHOT.jar
```

> Truy cập: [http://localhost:1111/](http://localhost:1111/)

---

## 👨‍💻 Key API Endpoints

| Endpoint                                      | Method | Desc                          |
| --------------------------------------------- | ------ | ----------------------------- |
| `/api/user/register`                          | POST   | Đăng ký user mới              |
| `/api/user/login`                             | POST   | Đăng nhập, trả về session     |
| `/api/user/getSession`                        | GET    | Kiểm tra trạng thái đăng nhập |
| `/api/{userID}/getCalendarItems/{yyyy-MM-dd}` | GET    | Lấy danh sách task từng ngày  |
| `/api/user/updatePassword`                    | POST   | Đổi mật khẩu (nếu cần)        |
| ...                                           | ...    | Xem chi tiết APIController    |

---

## 🛡️ Security

* **Spring Security** bảo vệ các route, chỉ cho phép public login/register & static
* **CSRF:** đang tắt mặc định khi dev, **nên bật khi production**
* Tất cả password lưu dạng hash BCrypt

---

## 📆 Workflow (How It Works)

1. User truy cập `/register` hoặc `/login` (public, không cần auth)
2. Đăng nhập thành công sẽ set session cookie
3. Mọi thao tác khác (list, task, calendar...) đều cần session
4. Frontend JS gọi API lấy task từng ngày, hiển thị lịch động (calendarView)
5. Logout hoặc hết session sẽ bị redirect lại login

---

## 📝 Contribution

* Fork repo, tạo branch feature/your-feature
* Commit, tạo Pull Request lên main
* Hãy thêm mô tả rõ chức năng, bugfix hoặc doc update

---

## 📄 License

MIT License © [VinhVRS](https://github.com/vinhvrs)

---

> **Feel free to star ⭐, fork, contribute or mở issue nếu cần support!**
