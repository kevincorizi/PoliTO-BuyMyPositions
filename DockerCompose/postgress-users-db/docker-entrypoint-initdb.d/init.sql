-- noinspection SqlNoDataSourceInspectionForFile
CREATE TABLE IF NOT EXISTS user_entity (
  username VARCHAR(20) PRIMARY KEY,
  password VARCHAR(60) NOT NULL,
  role     VARCHAR(20) NOT NULL,
  balance  NUMERIC DEFAULT 2000
);

INSERT INTO user_entity (username, password, role, balance)
VALUES ('max', '$2a$08$2nuDld5GCiNZHNoyCINT9egX7B7laQ6ZX67ings6LPbZXFQThoJ3C', 'USER', 2000);
INSERT INTO user_entity (username, password, role, balance)
VALUES ('mich', '$2a$08$BXIci5eZJ8Uk5uyMCzD9MeXjM0EyYp5lUhQAk37rQKvjt9Gs3aYR.', 'USER', 2000);
INSERT INTO user_entity (username, password, role, balance)
VALUES ('kevin', '$2a$08$mRTnOLsSR7.lwRJqViTib.0uUAKFWkfkM5K.VBMD5N2s.EbqEmG7W', 'USER', 2000);
INSERT INTO user_entity (username, password, role, balance)
VALUES ('angelo', '$2a$08$96ikXB2Qoec9z0sJW7/3EeVUR5dIgwTy1mK9l7zGrATb.IvacHcNS', 'USER', 2000);

CREATE DATABASE clientsdb;
