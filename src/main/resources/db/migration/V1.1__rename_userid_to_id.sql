ALTER TABLE users
    ADD id UUID;

-- Copy values from user_id to id
UPDATE users
SET id = user_id;

-- Make id NOT NULL
ALTER TABLE users
    ALTER COLUMN id SET NOT NULL;

DO
$$
    DECLARE
        constraint_name text;
    BEGIN
        SELECT tc.constraint_name
        INTO constraint_name
        FROM information_schema.table_constraints tc
        WHERE tc.table_name = 'users'
          AND tc.constraint_type = 'PRIMARY KEY';

        IF constraint_name IS NOT NULL THEN
            EXECUTE 'ALTER TABLE users DROP CONSTRAINT ' || constraint_name;
        END IF;
    END
$$;

ALTER TABLE users
    ADD CONSTRAINT pk_users PRIMARY KEY (id);

ALTER TABLE users
    DROP
        COLUMN user_id;

ALTER TABLE users
    ALTER
        COLUMN account_status TYPE VARCHAR(255) USING (account_status::VARCHAR(255));