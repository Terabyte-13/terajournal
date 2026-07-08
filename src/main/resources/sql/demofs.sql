CREATE TABLE IF NOT EXISTS demoFileSystem(
	id INT AUTO_INCREMENT PRIMARY KEY, -- ID del file.
	parent_id INT, -- ID della directory parente. NULL se il file si trova nel root.
	is_directory BOOLEAN,
	name VARCHAR(255), -- nome del file.
	data BLOB --blob contenente i dati del file. NULL se il file è una directory
);
