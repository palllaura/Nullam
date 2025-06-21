INSERT INTO event (id, name, time, location)
VALUES (
        9999,
        'Past event',
        '2025-05-27 19:33:00',
        'Kodu');

INSERT INTO person_participation (id, additional_info, payment_method, first_name, last_name, personal_code, event_id)
VALUES (
        900,
        'Info goes here',
        'BANK_TRANSFER',
        'Toomas',
        'Metsmaa',
        '36601019999',
        9999);

INSERT INTO company_participation (id, additional_info, payment_method, company_name, number_of_participants, registry_code, event_id)
VALUES (
        800,
        'Info goes here',
        'CASH',
        'Katusemehed OÜ',
        3,
        '12345678',
        9999);