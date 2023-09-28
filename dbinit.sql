USE ticketingdb;

-- Insert roles.
insert into roles(name) values ('ROLE_USER'), ('ROLE_ADMIN');

-- Setup user
-- User Password: pwd
INSERT INTO user (username, password, email, phone, country_of_residences, dob) VALUES 
('admin1', '$2a$12$CUGValkqjn0vte..M9wx0.cnnTgd4IH4k7JgGc5Wg8ZhSE05VhKtO', 'a1@admin.net', '88881111', 'Singapore', CURRENT_TIMESTAMP),
('admin2', '$2a$12$CUGValkqjn0vte..M9wx0.cnnTgd4IH4k7JgGc5Wg8ZhSE05VhKtO', 'a2@admin.net', '88882222', 'Singapore', CURRENT_TIMESTAMP),
('user1', '$2a$12$CUGValkqjn0vte..M9wx0.cnnTgd4IH4k7JgGc5Wg8ZhSE05VhKtO', 'u1@user.net', '99991111', 'Singapore', CURRENT_TIMESTAMP);

INSERT INTO user_roles(user_id, role_id) VALUES (1, 2), (2, 2), (3, 1);


-- Mock Business Data Setup
-- (Venue, Category, Section, Seat, Concert, CategoryPrice, ConcertImage)
-- For ConcertSession, please use the API to create them as there is business logic there.
INSERT INTO venue (name) VALUES ('Singapore Indoor Stadium'), ('Astral Express Live Stage');

INSERT INTO category (name, venue_id) VALUES 
('VIP', 1), ('Cat A', 1), ('Cat B', 1),
('Gold', 2), ('Purple', 2), ('Blue', 2);

INSERT INTO section (name, category_id) VALUES
('VIP-1', 1),
('Cat A-1', 2), ('Cat A-2', 2),
('Cat B-1', 3), ('Cat B-2', 3),
('Gold-1', 4),
('Purple-1', 5), ('Purple-2', 5),
('Blue-1', 6), ('Blue-2', 6);

INSERT INTO seat (section_id, seat_row, seat_number) VALUES
(1, 'A', 1), (1, 'A', 2), (1, 'A', 3), (1, 'A', 4), (1, 'A', 5),
(2, 'A', 1), (2, 'A', 2), (2, 'A', 3), (2, 'A', 4), (2, 'A', 5),
(3, 'A', 1), (3, 'A', 2), (3, 'A', 3), (3, 'A', 4), (3, 'A', 5),
(4, 'A', 1), (4, 'A', 2), (4, 'A', 3), (4, 'A', 4), (4, 'A', 5),
(5, 'A', 1), (5, 'A', 2), (5, 'A', 3), (5, 'A', 4), (5, 'A', 5),
(6, 'A', 1), (6, 'A', 2), (6, 'A', 3), (6, 'A', 4), (6, 'A', 5),
(7, 'A', 1), (7, 'A', 2), (7, 'A', 3), (7, 'A', 4), (7, 'A', 5),
(8, 'A', 1), (8, 'A', 2), (8, 'A', 3), (8, 'A', 4), (8, 'A', 5),
(9, 'A', 1), (9, 'A', 2), (9, 'A', 3), (9, 'A', 4), (9, 'A', 5),
(10, 'A', 1), (10, 'A', 2), (10, 'A', 3), (10, 'A', 4), (10, 'A', 5);

INSERT INTO concert (title, venue_id, description, artist) VALUES
('IU "Real Fantasy" SG', 1, 'IU\'s "Real Fantasy" Tour in Singapore', 'IU'),
('Landau\'s Glory', 2, 'The siblings of Landau bringing you the pride & passion of Belobog through Rock n\' Roll', 'Serval & Gepard');

INSERT INTO category_price (concert_id, category_id, price) VALUES
(1, 1, 149.99), (1, 2, 99.99), (1, 3, 79.99);

INSERT INTO concert_image (name, concert_id, file_path) VALUES 
('iu.jpeg_1695207946703', 1, '../images/iu.jpeg_1695207946703');
