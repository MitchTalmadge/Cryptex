-- Created by Vertabelo (http://vertabelo.com)
-- Created on Sat Mar 24 05:10:09 UTC 2018 by Mitch Talmadge

-- Source version: Cryptex, Fri Mar 23 07:35:11 UTC 2018 by Mitch Talmadge
-- Target version: Cryptex, Sat Mar 24 05:09:46 UTC 2018 by Mitch Talmadge


-- Table: telegram_chat_history
-- [#1] Primary key name was empty, using generated value.
CREATE TABLE "telegram_chat_history" (
  "id" int  NOT NULL,
  "phone_number" text  NOT NULL,
  "chat_id" int  NOT NULL,
  "last_message_id" int  NOT NULL,
  CONSTRAINT "telegram_chat_history_pk" PRIMARY KEY ("id")
);

