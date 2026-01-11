-- Création des bases
CREATE DATABASE IF NOT EXISTS db_user;
CREATE DATABASE IF NOT EXISTS db_book;
CREATE DATABASE IF NOT EXISTS db_emprunter;

-- Tables pour db_user
USE db_user;
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

-- Tables pour db_book
USE db_book;
CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    available BOOLEAN DEFAULT TRUE
);

-- Tables pour db_emprunter
USE db_emprunter;
CREATE TABLE IF NOT EXISTS emprunts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATE,
    return_date DATE,
    FOREIGN KEY (user_id) REFERENCES db_user.users(id),
    FOREIGN KEY (book_id) REFERENCES db_book.books(id)
);
