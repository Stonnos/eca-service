CREATE OR REPLACE FUNCTION migrate_classifiers_info() RETURNS VOID AS $$
DECLARE
  ev_log evaluation_log%rowtype;
  classifier_info_inserted_id BIGINT;
BEGIN
  FOR ev_log IN select * from evaluation_log
  LOOP
     insert into classifier_info(classifier_name) values(ev_log.classifier_name) returning id into classifier_info_inserted_id;
     update evaluation_log set classifier_info_id = classifier_info_inserted_id where id = ev_log.id;
     update classifier_input_options set classifier_info_id = classifier_info_inserted_id where evaluation_log_id = ev_log.id;
  END LOOP;
END;
$$
LANGUAGE plpgsql;

DO $$
BEGIN
  PERFORM migrate_classifiers_info();
END $$;

DROP function migrate_classifiers_info();
