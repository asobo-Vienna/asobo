INSERT INTO "event" (creation_date,"date",modification_date,creator_id,id,pictureuri,description,"location",title,is_private) VALUES
	 ('2026-01-18 12:34:50.452286','2026-01-25 16:00:30',NULL,'cff3d733-7df9-4443-bf59-b86c9875bdec'::uuid,'58e7618c-0573-43d7-8aa2-3efbd0acdee6'::uuid,'/uploads/event-cover-pictures/a60bc8e4-6a72-456e-b984-322a4d5b373f_bandb.jpg','Expect loud opinions, lowbrow commentary, and a surprising amount of “uhh… huh huh.” Educational value: zero. Entertainment value: regrettably high.','Highland, TX','Watching TV with Beavis & Butt-Head',false),
	 ('2025-07-11 19:33:23.682237','2025-07-13 13:00:00',NULL,NULL,'de32052d-645f-41d1-b43c-70621565ff7a'::uuid,'/uploads/event-cover-pictures/aaf0a06d-8f4a-4dca-ae8d-7d881882abd4_franzi.jpg','Cosplaying as a Viennese tram with the master cosplayer Franzi a.k.a. 5er','Vienna','Tram Cosplaying',false),
	 ('2025-11-21 13:06:01.293851','2025-11-22 19:03:00',NULL,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,'129b9ac0-451a-4092-ab16-67ff6f60b32f'::uuid,'/uploads/event-cover-pictures/7523dcc4-39c8-4b6b-8422-32eb5059638e_70sparty.jpg','70s Party! with your incredible hosts Boomer, Spaulding & Travis','El Segundo, CA','70s Party',false),
	 ('2025-05-21 13:59:21.048244','2025-06-21 20:00:00',NULL,'7767118c-19bd-4c28-8129-c0abda74b46c'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'/uploads/event-cover-pictures/f40bf03b-c55b-4c6a-801b-9b764fc99262_movie_night.jpg','Join us for the most bestest and extravagant movie night ever! Yeah! Bring your friends and don’t forget your own popcorn, nachos and booze...','San Juan Capistrano, CA','Movie Night',false),
	 ('2025-06-22 19:59:21.048244','2025-07-04 17:00:00',NULL,'cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid,'045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'/uploads/event-cover-pictures/55daceaa-0a26-407d-b709-d1df1cdd91f5_cooking.jpg','Cooking together brings people closer through creativity, teamwork, and shared joy — turning simple ingredients into lasting memories.','Vienna, AT','Cooking!',false),
	 ('2025-11-15 21:33:52.686221','2025-11-26 19:15:00',NULL,'e2540d46-4540-4223-b07c-78421260c91e'::uuid,'042081b2-1b0c-4759-bed6-8f54fe056130'::uuid,'/uploads/event-cover-pictures/f1e2868e-7f5f-401c-9b54-f24fb6717aa1_elrisitas_laughing.jpeg','Hilarious evening with a world renowned laughing coach.','Sevilla','Laughing with El Risitas',false),
	 ('2025-07-11 10:38:08.360601','2025-07-12 12:00:00',NULL,NULL,'ccc5d4bc-8d66-4d1a-87e7-a7b18b6c8773'::uuid,'/uploads/event-cover-pictures/d2dbe905-ee05-4b93-99e1-f438fb253c53_ozzy_bat.jpg','A delicious feast with the one and only Ozzy Osbourne','Birmingham','Bat Eating with Ozzy',true),
	 ('2025-07-11 10:55:03.235106','2025-07-13 17:00:00',NULL,NULL,'40a7f446-78e6-4383-b1cb-84d8f121011f'::uuid,'/uploads/event-cover-pictures/b6db941d-3c12-4519-8018-7a906e4b9bf6_bachlstmarx.jpg','Dope skate session at St. Marx skatepark','Vienna','Skateboarding St. Marx',true),
	 ('2025-11-16 00:53:10.518641','2025-11-21 19:00:00',NULL,'e2540d46-4540-4223-b07c-78421260c91e'::uuid,'735a5b18-55c6-4133-9b7b-4f5e47e97096'::uuid,'/uploads/event-cover-pictures/be32c33d-237c-4045-a9a7-0592d782f1a5_budspencer_terencehill_watschenbaum.png','Abhärtung durch das Kassieren von Stereowatschen von den professionellen Stereowatschenverteilern Bud Spencer und Terence Hill.','Reumannplatz, Wien','Am Watschenbaum rütteln mit Bud Spencer und Terence Hill',false),
	 ('2026-01-09 13:43:34.496926','2026-01-09 16:45:30',NULL,'cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid,'78cae4d2-b504-4455-9998-5c5b9f4d0435'::uuid,'/uploads/event-cover-pictures/2ac3b6d9-6834-4929-a066-f87457e3caa6_beatrix-kiddo.jpg','Kill Bill','Gotham City, NJ','Kill Bill',false),
	 ('2026-01-16 16:09:54.649093','2026-01-08 16:45:30',NULL,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,'94c6fbff-cb33-4f59-8fef-d298ec9e46d2'::uuid,'/uploads/event-cover-pictures/c082f713-c8b9-43d9-8e5b-12d4fd197a60_hockeymatchjason.jpg','Get ready for a great hockey lesson with one of the world''s most unique and acclaimed hockey players','Camp Crystal Lake, NJ','Hi',false)
ON CONFLICT (id) DO NOTHING;


INSERT INTO event_event_admins (administered_events_id,event_admins_id) VALUES
	 ('94c6fbff-cb33-4f59-8fef-d298ec9e46d2'::uuid,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('94c6fbff-cb33-4f59-8fef-d298ec9e46d2'::uuid,'cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('58e7618c-0573-43d7-8aa2-3efbd0acdee6'::uuid,'cff3d733-7df9-4443-bf59-b86c9875bdec'::uuid);


INSERT INTO event_participants (attended_events_id,participants_id) VALUES
	 ('045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'7767118c-19bd-4c28-8129-c0abda74b46c'::uuid),
	 ('045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid),
	 ('045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'f18e96c7-6416-4f51-87d5-4cd229715933'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'68362d07-aaf8-4d48-acab-699da557d231'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'cff3d733-7df9-4443-bf59-b86c9875bdec'::uuid),
	 ('045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'cff3d733-7df9-4443-bf59-b86c9875bdec'::uuid),
	 ('de32052d-645f-41d1-b43c-70621565ff7a'::uuid,'7767118c-19bd-4c28-8129-c0abda74b46c'::uuid),
	 ('de32052d-645f-41d1-b43c-70621565ff7a'::uuid,'cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid),
	 ('129b9ac0-451a-4092-ab16-67ff6f60b32f'::uuid,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'7767118c-19bd-4c28-8129-c0abda74b46c'::uuid),
	 ('735a5b18-55c6-4133-9b7b-4f5e47e97096'::uuid,'129d2530-228c-4da1-b89c-3189fc76f666'::uuid),
	 ('042081b2-1b0c-4759-bed6-8f54fe056130'::uuid,'e2540d46-4540-4223-b07c-78421260c91e'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'0219213a-e9ce-451b-9a60-d51fa22af669'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'e2540d46-4540-4223-b07c-78421260c91e'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid)
ON CONFLICT (attended_events_id, participants_id) DO NOTHING;


INSERT INTO medium (event_id,id,mediumuri,creator_id) VALUES
	 ('045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'88385e56-0677-4d2b-b80a-2b35069415ae'::uuid,'/uploads/event-galleries/045fbc61-736a-4be1-baa3-070748e07f17/ce6bc3d4-eb47-4a6f-8982-d91b103aa9b9_snoop.jpg','7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'42d805ac-7df3-4e96-8151-bf39cf78b684'::uuid,'/uploads/event-galleries/045fbc61-736a-4be1-baa3-070748e07f17/48b49ce8-3608-4369-a5a0-816abb187b51_therock.jpg','7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'3f702ae2-16e3-4023-85e9-ada98a308f20'::uuid,'/uploads/event-galleries/045fbc61-736a-4be1-baa3-070748e07f17/5f37cd30-d15b-4603-931e-1f330b037176_batman.jpg','7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'3d545152-7e5b-4df1-91c6-56c60ae0c6b4'::uuid,'/uploads/event-galleries/045fbc61-736a-4be1-baa3-070748e07f17/8d410f31-5c39-43fc-95c4-ed60b52534e7_miawallace.jpg','7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'3841398e-ab9a-40fe-a730-6377403cbf0c'::uuid,'/uploads/event-galleries/aaed8676-8b38-4c7a-b8b1-66dd683a1a96/bde7a89b-7b50-46a1-ad82-2e8901082f5b_undertaker.jpg','7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('ccc5d4bc-8d66-4d1a-87e7-a7b18b6c8773'::uuid,'c6b61a02-4956-4bbc-b6b4-d9b271dcda40'::uuid,'/uploads/event-galleries/ccc5d4bc-8d66-4d1a-87e7-a7b18b6c8773/c58a412a-09fb-4f16-938f-910e5fc6f646_bat.jpg','7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'8770f435-e220-43d9-9e05-02ea45f96d1e'::uuid,'/uploads/event-galleries/aaed8676-8b38-4c7a-b8b1-66dd683a1a96/bdc64566-d83c-4bfb-b488-854905def2db__DSC0416.jpg','7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid),
	 ('aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'93da385a-2641-451f-b4d1-048205fb8845'::uuid,'/uploads/event-galleries/aaed8676-8b38-4c7a-b8b1-66dd683a1a96/989e67c9-2aa5-4ac4-a905-9bb63ed7621c_tragedy-girls.jpg','cff3d733-7df9-4443-bf59-b86c9875bdec'::uuid),
	 ('58e7618c-0573-43d7-8aa2-3efbd0acdee6'::uuid,'57463a0c-6f02-4ba0-b93a-bf3c79b13bce'::uuid,'/uploads/event-galleries/58e7618c-0573-43d7-8aa2-3efbd0acdee6/8c0462c9-255e-40f2-b87a-2019d37c579a_b_b-hairspray.jpg','cff3d733-7df9-4443-bf59-b86c9875bdec'::uuid)
ON CONFLICT (id) DO NOTHING;

INSERT INTO "role" (id,"name") VALUES
	 (0,'SUPERADMIN'),
	 (1,'ADMIN'),
	 (2,'USER')
ON CONFLICT (id) DO NOTHING;


INSERT INTO user_comment (creation_date,modification_date,author_id,event_id,id,"text",pictureuri) VALUES
	 ('2025-07-11 19:39:34.391567','2025-12-05 18:10:16.065745','7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'34e14c80-41db-4de1-8e3f-fa7243bd0ff9'::uuid,'aaaaahhhhhhh!!!','/uploads/profile-pictures/4f4c9853-29e5-4d05-95c7-d85efc034fe8_harley.jpg'),
	 ('2025-09-14 19:18:15.024199','2025-12-05 18:10:31.05611','7767118c-19bd-4c28-8129-c0abda74b46c'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'631ec747-5460-4657-b57b-84435b8ab4b3'::uuid,'hi','/uploads/profile-pictures/67314290-8700-4f0f-94f4-0da7ed05dbf4_gg.jpg'),
	 ('2025-12-05 19:25:17.528035',NULL,'cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid,'045fbc61-736a-4be1-baa3-070748e07f17'::uuid,'1dbea455-baed-43f5-a225-69cffe1a3f50'::uuid,'Batman loves cooking!',NULL),
	 ('2025-10-11 09:41:05.80277',NULL,'7767118c-19bd-4c28-8129-c0abda74b46c'::uuid,'de32052d-645f-41d1-b43c-70621565ff7a'::uuid,'562a619b-e23b-45fe-8e82-e79aa448fcba'::uuid,'I like trams!','/uploads/profile-pictures/67314290-8700-4f0f-94f4-0da7ed05dbf4_gg.jpg'),
	 ('2025-10-11 09:58:42.903773',NULL,'cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid,'40a7f446-78e6-4383-b1cb-84d8f121011f'::uuid,'ab048699-df02-45d2-b2e6-4ca74e4fc862'::uuid,'Yo! I just bought a new skateboard. I''m gonna join you guys','/uploads/profile-pictures/6eaaa0c2-0e78-4171-a069-5e5fda5794d8_batman.jpg'),
	 ('2025-12-12 10:14:09.13668',NULL,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'29b54b7b-4d4c-4ca7-8e40-eb145409914f'::uuid,'hello',NULL),
	 ('2026-01-09 13:50:24.498985',NULL,'de89fe03-aaa9-4615-9f2f-a380aa31cba2'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'6b1ec5dc-16fc-4110-839a-9d72b44d275d'::uuid,'Hello, this is Austin',NULL),
	 ('2025-10-24 11:16:56.987394',NULL,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'e4592c20-6ef7-4f67-a360-9c7835c12699'::uuid,'<a href="https://google.com">Google</a>','/uploads/profile-pictures/4f4c9853-29e5-4d05-95c7-d85efc034fe8_harley.jpg'),
	 ('2025-10-24 15:01:53.821373',NULL,'0219213a-e9ce-451b-9a60-d51fa22af669'::uuid,'de32052d-645f-41d1-b43c-70621565ff7a'::uuid,'4126fc42-3567-49e6-9509-ef077c87a1a6'::uuid,'4 touchdowns in 1 game!','/uploads/profile-pictures/ee8e0968-095d-4899-895c-d008cd994b85_al-bundy.jpg'),
	 ('2026-01-18 13:04:20.355271',NULL,'cff3d733-7df9-4443-bf59-b86c9875bdec'::uuid,'58e7618c-0573-43d7-8aa2-3efbd0acdee6'::uuid,'5c42661b-c383-4cb0-a6a6-7b173a0f1edd'::uuid,'Wohoo! We''re so looking forward to that evening <3',NULL),
	 ('2025-07-20 16:48:43.222211',NULL,'7767118c-19bd-4c28-8129-c0abda74b46c'::uuid,'ccc5d4bc-8d66-4d1a-87e7-a7b18b6c8773'::uuid,'193a835b-ce74-4af6-8bda-4302b99a6f4c'::uuid,'Well that was a fun night!','/uploads/profile-pictures/67314290-8700-4f0f-94f4-0da7ed05dbf4_gg.jpg'),
	 ('2025-10-26 14:04:06.762288',NULL,'e2540d46-4540-4223-b07c-78421260c91e'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'640c8d94-1fa6-4adb-acbf-d6e5bcab9728'::uuid,'SELECT * FROM users WHERE 1=1;','/uploads/profile-pictures/08305516-c382-4120-a01d-54c8208a461e_skeletor.jpg'),
	 ('2025-10-31 15:32:43.492789',NULL,'0219213a-e9ce-451b-9a60-d51fa22af669'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'f8adae5c-5580-4b84-9f7b-eb6a00cc11fa'::uuid,'Yes it works!','/uploads/profile-pictures/ee8e0968-095d-4899-895c-d008cd994b85_al-bundy.jpg'),
	 ('2025-10-31 20:45:31.696369',NULL,'e2540d46-4540-4223-b07c-78421260c91e'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'86f92040-c28a-4b64-b584-0224197f6236'::uuid,'SKELECT * FROM skelector;','/uploads/profile-pictures/08305516-c382-4120-a01d-54c8208a461e_skeletor.jpg'),
	 ('2025-11-17 09:40:14.545612',NULL,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,'40a7f446-78e6-4383-b1cb-84d8f121011f'::uuid,'e08455de-a76a-496e-88e1-5506326fa6ca'::uuid,'Bats Shmats!','/uploads/profile-pictures/4f4c9853-29e5-4d05-95c7-d85efc034fe8_harley.jpg'),
	 ('2025-11-21 11:02:17.051976',NULL,'e2540d46-4540-4223-b07c-78421260c91e'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'d1fb6b70-6f4d-4471-8ddb-2c5fd955d5cf'::uuid,'test','/uploads/profile-pictures/138e74d0-71b1-430a-92da-0943ae388170_skeletor2.jpg'),
	 ('2025-11-21 13:06:52.252766',NULL,'7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,'129b9ac0-451a-4092-ab16-67ff6f60b32f'::uuid,'70e4e478-f2d5-461a-bc26-6c3c909c05ca'::uuid,'Sounds great, I''m in!','/uploads/profile-pictures/4f4c9853-29e5-4d05-95c7-d85efc034fe8_harley.jpg'),
	 ('2025-10-11 13:23:21.187355','2025-11-28 18:39:30.633556','cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid,'aaed8676-8b38-4c7a-b8b1-66dd683a1a96'::uuid,'772b857f-3af8-4c2d-9ca6-984d73eae1f8'::uuid,'Batman likes to watch Batman movies because Batman knows Batman delivers the best Batman performances. Alright?','/uploads/profile-pictures/6eaaa0c2-0e78-4171-a069-5e5fda5794d8_batman.jpg')
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id,role_id) VALUES
	 ('a4235f35-d4b9-4d31-b4f1-91b8a6436d98'::uuid,2),
	 ('68362d07-aaf8-4d48-acab-699da557d231'::uuid,2),
	 ('5af1fd99-2327-4da5-bd08-99faf78dffe8'::uuid,2),
	 ('e2540d46-4540-4223-b07c-78421260c91e'::uuid,0),
	 ('e2540d46-4540-4223-b07c-78421260c91e'::uuid,1),
	 ('e2540d46-4540-4223-b07c-78421260c91e'::uuid,2),
	 ('7d3e3ec5-e2ab-4f52-b75a-90ddf8d0e806'::uuid,2),
	 ('8ddd3f63-7b36-402f-bbaa-5cec11d81863'::uuid,2),
	 ('f18e96c7-6416-4f51-87d5-4cd229715933'::uuid,2),
	 ('5220c739-9633-4335-b76b-244dfacec1ae'::uuid,2),
	 ('cd14af3d-534a-47a0-9bb1-4776a576684b'::uuid,2),
	 ('c1d425c7-9c86-4f89-92bd-f131fc5caf24'::uuid,2),
	 ('f75d02b0-ca03-4c0d-af93-acae8aea85f7'::uuid,2),
	 ('9d6fa8e4-fd6d-4c25-9439-8ee91077cf47'::uuid,2),
	 ('0219213a-e9ce-451b-9a60-d51fa22af669'::uuid,2),
	 ('01a26044-6c60-40b6-a449-c72b0d240503'::uuid,2),
	 ('d8c038cc-4965-437f-ac37-4865ba4510dd'::uuid,2),
	 ('5c8137d6-eb24-4c34-87d1-38b844281de6'::uuid,2),
	 ('7991fed0-9b75-4d9c-ae0f-5c385217f4f1'::uuid,2),
	 ('8cdb85c8-01f2-4208-9b91-a0f44f9fb6d4'::uuid,2),
	 ('cff3d733-7df9-4443-bf59-b86c9875bdec'::uuid,2),
	 ('129d2530-228c-4da1-b89c-3189fc76f666'::uuid,2),
	 ('7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,2),
	 ('7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,0),
	 ('7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,1),
	 ('6b533028-27ca-4e5d-974a-5f89eca76f1d'::uuid,2),
	 ('6b533028-27ca-4e5d-974a-5f89eca76f1d'::uuid,1),
	 ('91740f77-0b27-4db8-a4b8-5b2b62a25664'::uuid,2),
	 ('91740f77-0b27-4db8-a4b8-5b2b62a25664'::uuid,1),
	 ('573cb207-ea43-49f4-add2-89e078772908'::uuid,1),
	 ('573cb207-ea43-49f4-add2-89e078772908'::uuid,2),
	 ('0e8f4193-5c7f-4e84-8d2c-4c0b7609cced'::uuid,2),
	 ('cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid,2),
	 ('de89fe03-aaa9-4615-9f2f-a380aa31cba2'::uuid,2),
	 ('3fb77b1c-8670-40a2-929b-2b021f6e3e61'::uuid,2),
	 ('3fb77b1c-8670-40a2-929b-2b021f6e3e61'::uuid,1),
	 ('7767118c-19bd-4c28-8129-c0abda74b46c'::uuid,2);


INSERT INTO users (is_active,register_date,id,pictureuri,email,first_name,"location",old_password,"password",salutation,surname,username,about_me) VALUES
	 (true,'2025-06-21 12:28:08.064861','7da69d8e-55c7-4a96-ac6d-cb207e4e8a21'::uuid,'/uploads/profile-pictures/4f4c9853-29e5-4d05-95c7-d85efc034fe8_harley.jpg','harley@haha.com','Harleen','Brooklyn, NY',NULL,'$2a$10$pPa/hEuxrOOfAOga7LBU4uXhL7n6zb/tHzuLHZzAeREBrGATRfH8i','Ms.','Quinzel','harley',NULL),
	 (true,'2025-06-21 12:48:46.048244','7767118c-19bd-4c28-8129-c0abda74b46c'::uuid,'/uploads/profile-pictures/67314290-8700-4f0f-94f4-0da7ed05dbf4_gg.jpg','gg@ggallin.com','GG','Lancaster, NH',NULL,'$2a$10$D8rKBvbxlFu6aqbGBJhO1edbYgGL61uqxnbKqEeKnvOuX4PcWZbZK','Mr.','Allin','ggallin',NULL),
	 (false,'2025-07-07 00:04:49.973131','3fb77b1c-8670-40a2-929b-2b021f6e3e61'::uuid,'/uploads/profile-pictures/0c6e58ad-9426-46ef-8762-b8050317485e_waynearnold.jpg','wayne@arnold.com','Wayne','Sunnyville',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Mr.','Arnold','wayne',NULL),
	 (true,'2025-10-11 00:31:55.417063','573cb207-ea43-49f4-add2-89e078772908'::uuid,'/uploads/profile-pictures/00ea7946-79aa-4c38-8206-deaa972e3958_breitfuss.jpg','breitfuss@ma2412.at','Engelbert','Vienna',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Ing.','Breitfuß','Bertl',NULL),
	 (true,'2025-10-11 01:21:45.8209','6b533028-27ca-4e5d-974a-5f89eca76f1d'::uuid,'/uploads/profile-pictures/188ec08d-40fb-48ec-a02f-355402292b38_hercules.jpg','hercules@olympia.gr','Hercules','New York',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Mr.','Herakles','hercules',NULL),
	 (true,'2025-06-21 11:04:07.308963','cb0d70b4-8ac6-4045-8a77-55be2583f2a8'::uuid,'/uploads/profile-pictures/6eaaa0c2-0e78-4171-a069-5e5fda5794d8_batman.jpg','batman@batcave.com','Bruce','Gotham City, NJ',NULL,'$2a$10$5w1WYzs.b9TG2owTTE6c2eKgbsuJ3UuDlLYhdsOrC4RPIiZBxOzBC','Mr.','Wayne','batman',NULL),
	 (false,'2025-07-11 18:18:58.15966','91740f77-0b27-4db8-a4b8-5b2b62a25664'::uuid,'/uploads/profile-pictures/911ae559-2c12-44d3-bd50-9b556e6d3885_franzi.jpg','fuenfer@wienerlinien.at','Franzi','Wien',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Mr.','Mayerhofer','5er',NULL),
	 (true,'2025-06-22 17:07:37.69455','a4235f35-d4b9-4d31-b4f1-91b8a6436d98'::uuid,'/uploads/profile-pictures/c0e1d8e9-b9d1-4060-a893-79b148366010_eltonjohn.jpg','elton@john.co.uk','Elton','London',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Mr.','John','sireltonjohn',NULL),
	 (true,'2025-11-15 12:34:57.23755','129d2530-228c-4da1-b89c-3189fc76f666'::uuid,'/uploads/profile-pictures/75b3ac4b-4140-4439-8efd-b9185a69aefc_snoop.jpg','snoopdogg@deathrow.com','Calvin','Long Beach, CA',NULL,'$2a$10$yPG3MxaRKdvG6.hL/35al.u3hrv.VRGDfk5p/d00h8vHQVpKAEpq.','Mr.','Broadus','snoopdogg',NULL),
	 (false,'2025-11-20 23:39:03.446264','5af1fd99-2327-4da5-bd08-99faf78dffe8'::uuid,'/uploads/profile-pictures/62122e5a-2f90-4244-9c51-15c5471d8e6f_kottan.jpg','kottan@polizei.gv.at','Adolf','Wien',NULL,'$2a$10$/DvKPEwCC.OqXUiOHJo5yeQXtu49fH8FMn4TU.soaWU5qcti/.CEe','Major','Kottan','Kottan',NULL),
	 (true,'2025-07-06 23:59:05.635171','f18e96c7-6416-4f51-87d5-4cd229715933'::uuid,'/uploads/profile-pictures/f18e96c7-6416-4f51-87d5-4cd229715933_blackknight.jpg','knight@black.co.uk','Black','Wales',NULL,'$2a$10$TMWMRG70eUohMTq.0W4OjeYjXLhnRwYVl3tnDWAuACNNmHfmdummq','Mr.','Knight','blackknight',NULL),
	 (false,'2025-07-04 15:40:49.970751','f75d02b0-ca03-4c0d-af93-acae8aea85f7'::uuid,'/uploads/profile-pictures/bcf033ff-550f-49c4-9159-41ae727b1e02_HarryP.jpg','potter@gmail.com','Harry','Hogwards',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Mr.','Potter','HarryP',NULL),
	 (false,'2025-07-06 23:47:48.970154','9d6fa8e4-fd6d-4c25-9439-8ee91077cf47'::uuid,'/uploads/profile-pictures/0d317296-297b-4108-aa10-ab24a7553d9e_ninahagen.jpg','nina@hagen.de','Nina','Berlin',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Ms.','Hagen','ninahagen',NULL),
	 (true,'2025-10-17 15:47:58.174168','0219213a-e9ce-451b-9a60-d51fa22af669'::uuid,'/uploads/profile-pictures/ee8e0968-095d-4899-895c-d008cd994b85_al-bundy.jpg','al@footlocker.com','Al','Chicago',NULL,'$2a$10$sLt2rEIC6cb/joChR3IUDutyi8nLOrb1PaxsxowuiPqa.HLcLiBM.','Mr.','Bundy','albundy',NULL),
	 (true,'2025-07-04 15:38:01.15607','01a26044-6c60-40b6-a449-c72b0d240503'::uuid,'/uploads/profile-pictures/ab23a9fa-a782-47f8-ab10-bd612316efb8_ozzy.jpg','ozzy@blacksabbath.co.uk','Ozzy','Birmingham',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Mr.','Osbourne','bateater',NULL),
	 (true,'2025-07-06 23:54:56.253414','d8c038cc-4965-437f-ac37-4865ba4510dd'::uuid,'/uploads/profile-pictures/a8ea86eb-dc39-4742-8a7e-1808f4f48c75_Alex_DeLarge.jpg','alex@clockwork.com','Alex','London',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Mr.','DeLarge','alexlikesmolokoplus',NULL),
	 (false,'2025-11-21 12:59:34.748329','5c8137d6-eb24-4c34-87d1-38b844281de6'::uuid,'/uploads/profile-pictures/c0aa9e27-892e-4392-b9d8-b4ef6c7ba202_peggybundy.gif','peggy.bundy@gmail.com','Peggy','Chicago, IL',NULL,'$2a$10$z479o6QLetXTh0KTeG4NjOB6cz95vH2v/kfw6s7ad3zpylMOXvZ92','Ms.','Bundy','peggy',NULL),
	 (false,'2025-12-04 23:02:25.02838','8ddd3f63-7b36-402f-bbaa-5cec11d81863'::uuid,'/uploads/profile-pictures/9b8306c3-e9e5-451f-bf9b-e9303151c45d_derekturnbow.webp','derek@strangerthings.com','Derek','Hawkings, IN',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Mr.','Turnbow','dipshitderek',NULL),
	 (false,'2025-10-28 00:25:47.844324','7991fed0-9b75-4d9c-ae0f-5c385217f4f1'::uuid,'/uploads/profile-pictures/ebb7fe0a-930d-4f6b-bab0-e8f858e552d0_risitas.jpg','elrisitas@laughing.es','Juan Joya','Sevilla',NULL,'$2a$10$Rex4YmHSfedbq179oPsir.RCgCxCLZozjFnUqlOKT5iDzdbDfcMZy','Señor','Borja','elrisitas','Hahahaha!'),
	 (false,'2025-09-19 19:18:36.430076','e2540d46-4540-4223-b07c-78421260c91e'::uuid,'/uploads/profile-pictures/138e74d0-71b1-430a-92da-0943ae388170_skeletor2.jpg','skeletor@mastersoftheuniverse.com','Skele','Universe',NULL,'$2a$10$uZsdZk1BxJlafYhYe3QnH.uTHSsK2a89RJmFeQW5OQwEYT4CSI6uO','Master','Tor','skeletor','I am the mighty Skeletor, right?'),
	 (false,'2025-12-04 23:27:54.355396','7d3e3ec5-e2ab-4f52-b75a-90ddf8d0e806'::uuid,'/uploads/profile-pictures/f5c065f3-9056-4ef0-9e0f-fa9c0cbe06fb_beetlejuice.jpg','beet@juice.com','Lester','New York City',NULL,'$2a$10$oN8WOlONNLGwn9uRRfId3OqMsQzoNgkrV2PsSNqKPCOgA21RUJy0i','Mr.','Green','beetlejuice','Just hangin'' around!'),
	 (false,'2025-12-11 01:02:58.068706','cd14af3d-534a-47a0-9bb1-4776a576684b'::uuid,'/uploads/profile-pictures/be16b736-079e-49cd-b9ef-76f169fc169f_klimt.jpg','klimt@secession.at','Gustav','Vienna',NULL,'$2a$10$WUXpm70Uzq9g4aZQaHZsKuZ0IvXZjjfg3sjRxGe2uyCj0eFOSwg5W','Mr.','Klimt','klimtzug',NULL),
	 (false,'2025-12-11 00:54:33.878755','5220c739-9633-4335-b76b-244dfacec1ae'::uuid,'/uploads/profile-pictures/8b79b222-faf1-416d-b171-ca7a4b587fb4_neukirchner.jpg','guenther@sturmgraz.at','Günther','Graz',NULL,'$2a$10$GWcONbn6ykPayiP/HFGSce0giZiKIIX99eP17KoXH3XTaEmUTgKQe','Mr.','Neukirchner','neuguenea','Kane depperten Frogn bitte!'),
	 (false,'2025-12-11 01:09:47.051583','c1d425c7-9c86-4f89-92bd-f131fc5caf24'::uuid,'/uploads/profile-pictures/59af9417-88cf-4638-9a93-48d5098e916b_iggypop.jpg','iggy@pop.com','James','Muskegan, MI',NULL,'$2a$10$TujIi.JEO6Yjk8R9Xxefd.0z3OcQfupWNBhdCxjryX6BBgF5Gfcie','Mr.','Osterberg','iggypop',NULL),
	 (false,'2026-01-05 14:33:28.309123','0e8f4193-5c7f-4e84-8d2c-4c0b7609cced'::uuid,'/uploads/profile-pictures/a1db9993-e7a6-4a39-913b-0a69ee0f0bf1_robin.jpg','robin@strangerthings.com','Robin','Hawkings, IN',NULL,'$2a$10$KJfVayu52pUlXBuAuLXBneW/y9CZH4VlKDMHtsaV.H6h7Z.x7MKF.','Ms.','Buckley','robinbuckley',NULL),
	 (false,'2026-01-09 13:49:05.472848','de89fe03-aaa9-4615-9f2f-a380aa31cba2'::uuid,'/uploads/profile-pictures/f5c373eb-31a4-414d-8528-015773ab8288_austin-powers2.jpg','austin@gmail.com','Austin','England',NULL,'$2a$10$3ug6aDItSwflnSLwDTvhhuFOoiVx6ZwfPxuIE2YfWQj7i18cQPUM2','Mr.','Powers','austin',NULL),
	 (false,'2026-01-16 22:39:36.747414','68362d07-aaf8-4d48-acab-699da557d231'::uuid,'/uploads/profile-pictures/f57fe85e-5290-40a8-8f08-3c0cf224f28a_machoman.jpg','randysavage@wwf.com','Randy','Lexington, KY',NULL,'$2a$10$N9/kz0T8Oy7jeEe8krD3qemjMe/IEzrl0Sa5qyRkcEIsHkHCo.ibS','Mr.','Savage','machoman','Ooooh yeah!'),
	 (true,'2026-01-17 17:51:30.079191','8cdb85c8-01f2-4208-9b91-a0f44f9fb6d4'::uuid,'/uploads/profile-pictures/ead32bae-7bec-49aa-9932-050dd59c4946_doechii-alligator.jpg','doechii@gmail.com','Jaylah Ji''mya','Los Angeles, CA',NULL,'$2a$10$oJz0LgwU3g98SPAQZK/7FOccmjIp7CF.vJBYUemtEvuc54rl97Ciu','Ms.','Hickmon','doechii',NULL),
	 (true,'2026-01-17 18:00:08.354955','cff3d733-7df9-4443-bf59-b86c9875bdec'::uuid,'/uploads/profile-pictures/5ee1e913-5c77-4169-b3c0-efa8ef64c113_tragedy-girls.jpg','trgdgrrrlz@gmail.com','Tragedy','Rosedale',NULL,'$2a$10$CBwJDOMNsmH1rUTwPGXoWeeItIWoBJjGvGfJAzoKzWCE8GFtacHj.','Girls','Girls','tragedygirlz','To make an omelette, you have to kill some ex-boyfriends.')
ON CONFLICT (id) DO NOTHING;
