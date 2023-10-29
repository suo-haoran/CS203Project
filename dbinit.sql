USE ticketingdb;

-- Insert roles.
insert into role(name) values ('ROLE_USER'), ('ROLE_ADMIN');

-- Setup user
-- User Password: pwd
INSERT INTO user (username, password, email, phone, country_of_residences, dob) VALUES 
('admin1', '$2a$12$CUGValkqjn0vte..M9wx0.cnnTgd4IH4k7JgGc5Wg8ZhSE05VhKtO', 'a1@admin.net', '88881111', 'Singapore', CURRENT_TIMESTAMP),
('admin2', '$2a$12$CUGValkqjn0vte..M9wx0.cnnTgd4IH4k7JgGc5Wg8ZhSE05VhKtO', 'a2@admin.net', '88882222', 'Singapore', CURRENT_TIMESTAMP),
('user1', '$2a$12$CUGValkqjn0vte..M9wx0.cnnTgd4IH4k7JgGc5Wg8ZhSE05VhKtO', 'u1@user.net', '99991111', 'Singapore', CURRENT_TIMESTAMP);

INSERT INTO user_role(user_id, role_id) VALUES (1, 2), (2, 2), (3, 1);


-- Mock Business Data Setup
-- (Venue, Category, Section, Seat, Concert, CategoryPrice, ConcertImage)
-- For ConcertSession, please use the API to create them as there is business logic there.
INSERT INTO venue (name) VALUES ('Singapore Indoor Stadium'), ('Astral Express Live Stage');

INSERT INTO category (name, venue_id) VALUES 
('FREE-TY QUEEN PACKAGE (STANDING)*', 1), ('FREE-TY LOVE PACKAGE (STANDING)*', 1), ('FREE-TY NEVERLAND PACKAGE (STANDING)*', 1),
('CAT 1 (STANDING)', 1), ('CAT 2', 1), ('CAT 3', 1), ('CAT 4', 1),
('CAT 5', 1), ('CAT 6 (RESTRICTED VIEW)', 1), ('CAT 7 (RESTRICTED VIEW)', 1);

INSERT INTO section (name, category_id) VALUES
("field_318", 7),
("field_319", 7),
("field_320", 7),
("field_322", 7),
("field_323", 7),
("field_324", 7),
("field_328", 8),
("field_329", 8),
("field_330", 8),
("field_331", 8),
("field_332", 8),
("field_310", 8),
("field_311", 8),
("field_312", 8),
("field_313", 8),
("field_314", 8),
("field_309", 10),
("field_333", 10),
("field_209", 9),
("field_233", 9),
("field_216", 6),
("field_217", 6),
("field_225", 6),
("field_226", 6),
("field_224", 5),
("field_223", 5),
("field_222", 5),
("field_221", 5),
("field_220", 5),
("field_219", 5),
("field_218", 5),
("field_215", 5),
("field_214", 5),
("field_213", 5),
("field_212", 5),
("field_211", 5),
("field_210", 5),
("field_227", 5),
("field_228", 5),
("field_229", 5),
("field_230", 5),
("field_231", 5),
("field_232", 5),
("field_PENB", 4),
("field_PEND", 4),
("field_PENC", 4),
("field_PENA", 4),
("field_PEND_NEVERLAND", 3),
("field_PENC_NEVERLAND", 3),
("field_PENA_NEVERLAND", 3),
("field_PENB_NEVERLAND", 3),
("field_PENC_LOVE", 2),
("field_PENA_LOVE", 2),
("field_PENB_LOVE", 2),
("field_PEND_LOVE", 2),
("field_PEND_QUEEN", 1),
("field_PENC_QUEEN", 1),
("field_PENA_QUEEN", 1),
("field_PENB_QUEEN", 1);



-- INSERT INTO seat (section_id, seat_row, seat_number) VALUES
-- (1, 'A', 1), (1, 'A', 2), (1, 'A', 3), (1, 'A', 4), (1, 'A', 5),
-- (2, 'A', 1), (2, 'A', 2), (2, 'A', 3), (2, 'A', 4), (2, 'A', 5),
-- (3, 'A', 1), (3, 'A', 2), (3, 'A', 3), (3, 'A', 4), (3, 'A', 5),
-- (4, 'A', 1), (4, 'A', 2), (4, 'A', 3), (4, 'A', 4), (4, 'A', 5),
-- (5, 'A', 1), (5, 'A', 2), (5, 'A', 3), (5, 'A', 4), (5, 'A', 5),
-- (6, 'A', 1), (6, 'A', 2), (6, 'A', 3), (6, 'A', 4), (6, 'A', 5),
-- (7, 'A', 1), (7, 'A', 2), (7, 'A', 3), (7, 'A', 4), (7, 'A', 5),
-- (8, 'A', 1), (8, 'A', 2), (8, 'A', 3), (8, 'A', 4), (8, 'A', 5),
-- (9, 'A', 1), (9, 'A', 2), (9, 'A', 3), (9, 'A', 4), (9, 'A', 5),
-- (10, 'A', 1), (10, 'A', 2), (10, 'A', 3), (10, 'A', 4), (10, 'A', 5);

DROP PROCEDURE IF EXISTS INSERTSEATS;
DELIMITER $$  
CREATE PROCEDURE INSERTSEATS()
   BEGIN
      DECLARE i INT Default 1 ;
      simple_loop: LOOP         
         insert into seat(section_id, seat_row, seat_number) values (i, 'A', 1), (i, 'A', 2), (i, 'A', 3), (i, 'A', 4), (i, 'A', 5);
         SET i=i+1;
         IF i=60 THEN
            LEAVE simple_loop;
         END IF;
   END LOOP simple_loop;
END $$
DELIMITER ;
call INSERTSEATS();
DROP PROCEDURE IF EXISTS INSERTSEATS;


INSERT INTO concert (title, venue_id, description, artist) VALUES
('IU "Real Fantasy" SG', 1, 'IU\'s "Real Fantasy" Tour in Singapore', 'IU'),
('Landau\'s Glory', 2, 'The siblings of Landau bringing you the pride & passion of Belobog through Rock n\' Roll', 'Serval & Gepard');

INSERT INTO category_price (concert_id, category_id, price) VALUES
(1, 1, 348), (1, 2, 328), (1, 3, 308),
(1, 4, 288), (1, 5, 288), (1, 6, 248),
(1, 7, 204), (1, 8, 178), (1, 9, 248),
(1, 10, 178);

-- Concert Image need to upload via postman manually.
