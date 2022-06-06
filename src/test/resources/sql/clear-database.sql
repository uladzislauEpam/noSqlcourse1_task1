delete
from tickets;

delete
from user_accounts;

delete
from users;

delete
from events;

TRUNCATE TABLE users RESTART IDENTITY CASCADE;
TRUNCATE TABLE events RESTART IDENTITY CASCADE;
TRUNCATE TABLE tickets RESTART IDENTITY;
TRUNCATE TABLE user_accounts RESTART IDENTITY;
