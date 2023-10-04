insert into message_template values('NEW_EXPERIMENT_PUSH_MESSAGE', 'Поступила новая заявка на эксперимент ${experiment.requestId}');
insert into message_template values('IN_PROGRESS_EXPERIMENT_PUSH_MESSAGE', 'Заявка на эксперимент ${experiment.requestId} поступила в работу');
insert into message_template values('FINISHED_EXPERIMENT_PUSH_MESSAGE', 'Эксперимент ${experiment.requestId} успешно завершен');
insert into message_template values('ERROR_EXPERIMENT_PUSH_MESSAGE', 'Эксперимент ${experiment.requestId} завершился с ошибкой');