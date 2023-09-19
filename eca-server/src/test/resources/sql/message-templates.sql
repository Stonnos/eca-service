insert into message_template values('NEW_EXPERIMENT', 'Поступила новая заявка на эксперимент ${experiment.requestId}');
insert into message_template values('IN_PROGRESS_EXPERIMENT', 'Заявка на эксперимент ${experiment.requestId} поступила в работу');
insert into message_template values('FINISHED_EXPERIMENT', 'Эксперимент ${experiment.requestId} успешно завершен');
insert into message_template values('ERROR_EXPERIMENT', 'Эксперимент ${experiment.requestId} завершился с ошибкой');