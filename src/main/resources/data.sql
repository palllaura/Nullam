INSERT INTO event (id, name, time, location)
VALUES (
        9999,
        'Pärnu linnajooks',
        '2025-05-27 19:33:00',
        'Pärnu');

INSERT INTO person_participation (id, additional_info, payment_method, first_name, last_name, personal_code, event_id)
VALUES (
        900,
        'Halva ilma korral ei osale',
        'BANK_TRANSFER',
        'Toomas',
        'Metsmaa',
        '36601019999',
        9999);

INSERT INTO company_participation (id, additional_info, payment_method, company_name, number_of_participants, registry_code, event_id)
VALUES (
        800,
        'Tuleme eraldi autodega',
        'CASH',
        'Katusemehed OÜ',
        3,
        '12345678',
        9999);

INSERT INTO event (id, name, time, location)
VALUES (
           8888,
           'Raamatuklubi kokkutulek',
           '2025-09-27 19:33:00',
           'Kopli');

INSERT INTO person_participation (id, additional_info, payment_method, first_name, last_name, personal_code, event_id)
VALUES (
           700,
           'Ei loe raamatuid, tulen seltskonna pärast',
           'BANK_TRANSFER',
           'Helle',
           'Hein',
           '36601019999',
           8888);

INSERT INTO company_participation (id, additional_info, payment_method, company_name, number_of_participants, registry_code, event_id)
VALUES (
           600,
           'Harri võtab oma koera kaasa',
           'CASH',
           'Raamatusõbrad MTÜ',
           7,
           '12345678',
           8888);