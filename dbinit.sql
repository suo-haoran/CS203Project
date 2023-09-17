# Insert roles.
INSERT INTO roles VALUES (null, 'ROLE_USER'), (null, 'ROLE_MODERATOR'), (null, 'ROLE_ADMIN');

# Setup venue and concert
insert into venue(name) value ('Bird\'s Nest');
insert into concert(artist, description, title, venue_id) value ('IU', 'An IU\'s concert', 'IU\'s Concert', 1);