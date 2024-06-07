CREATE OR REPLACE FUNCTION migrate_user_photo_ids() RETURNS VOID AS $$
DECLARE
  u_photo user_photo%rowtype;
BEGIN
  FOR u_photo IN select * from user_photo
  LOOP
     update user_entity set photo_id = u_photo.id where id = u_photo.user_id;
  END LOOP;
END;
$$
LANGUAGE plpgsql;

DO $$
BEGIN
  PERFORM migrate_user_photo_ids();
END $$;

DROP function migrate_user_photo_ids();
