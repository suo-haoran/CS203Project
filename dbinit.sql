-- Insert roles.
insert into roles(name) values ('ROLE_USER'), ('ROLE_ADMIN');
-- Setup user
-- User Password: test123
insert into user(username, password, email, phone, country_of_residences, dob) value ('testuser1', '$2a$10$3QF/AB6AR3UK2sT8cnZm9uNwPOCrGtn1k3WP98cOMGDRzsG4mZlGS', 'example@gmail.com', '+6512345768', 'SG', '1999-01-01');
insert into user_roles(user_id, role_id) value (1, 2);

-- Setup venue, concert and seats
insert into venue(name) value ('Singapore Indoor Stadium');
insert into section(name, venue_id) values ('Section1', 1), ('Section2', 1), ('Section3', 1);
insert into concert(artist, description, title, venue_id) value ('IU', 'An IU\'s concert', 'IU\'s Concert', 1);
insert into section_price(price, section_id, concert_id) values (300.00, 1, 1), (400.00, 2, 1), (500.00, 3, 1);
insert into seat(seat_number, seat_row, section_id) values (1, 'A', 1), (2, 'A', 1), (3, 'A', 1), (4, 'A', 1), (1, 'A', 2), (2, 'A', 2), (3, 'A', 2), (4, 'A', 2), (1, 'A', 3), (2, 'A', 3), (3, 'A', 3), (4, 'A', 3);
insert into concert_image(name, concert_id, file_path) value ('iu.jpeg_1695207946703', 1, '../images/iu.jpeg_1695207946703');
insert into concert_session(datetime, concert_id) values ('2030-01-01 00:00:00', 1), ('2030-01-01 01:00:00', 1) ,('2030-01-01 02:00:00', 1);
