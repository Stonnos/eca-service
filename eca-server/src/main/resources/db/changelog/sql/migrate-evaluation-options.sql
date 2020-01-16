CREATE OR REPLACE FUNCTION migrate_evaluation_options() RETURNS VOID AS $$
DECLARE
  ev_log evaluation_log%rowtype;
  num_folds_val integer;
  num_tests_val integer;
  seed_val integer;
BEGIN
  FOR ev_log IN select * from evaluation_log where evaluation_method = 'CROSS_VALIDATION'
  LOOP
     select cast ((select evaluation_option_value from evaluation_options where evaluation_log_id = ev_log.id and evaluation_option_name = 'NUM_FOLDS') AS INTEGER) into num_folds_val;
     select cast ((select evaluation_option_value from evaluation_options where evaluation_log_id = ev_log.id and evaluation_option_name = 'NUM_TESTS') AS INTEGER) into num_tests_val;
     select cast ((select evaluation_option_value from evaluation_options where evaluation_log_id = ev_log.id and evaluation_option_name = 'SEED') AS INTEGER) into seed_val;
     update evaluation_log set num_folds = num_folds_val, num_tests = num_tests_val, seed = seed_val where id = ev_log.id;
  END LOOP;
END;
$$
LANGUAGE plpgsql;

DO $$
BEGIN
  PERFORM migrate_evaluation_options();
END $$;

DROP function migrate_evaluation_options();
