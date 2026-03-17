
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'trungkien')
BEGIN
    CREATE DATABASE trungkien;
END
GO

USE trungkien;
GO


IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Users]') AND type in (N'U'))
BEGIN
    CREATE TABLE Users (
        Username NVARCHAR(50) PRIMARY KEY,
        Password NVARCHAR(50) NOT NULL
    );
    INSERT INTO Users VALUES ('admin', '29082006'), ('trungkien', '123');
END


IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Messages]') AND type in (N'U'))
BEGIN
    CREATE TABLE Messages (
        ID INT IDENTITY(1,1) PRIMARY KEY,
        Sender NVARCHAR(50),
        Content NVARCHAR(MAX),
        TimeSent DATETIME DEFAULT GETDATE()
    );
END