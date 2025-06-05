create table if not exists demoFileSystem(
	id INT AUTO_INCREMENT PRIMARY KEY, -- ID del file. AUTO_INCREMENT genera automaticamente un valore unico; PRIMARY KEY indica che è una chiave per identificare il field.
	parent_id INT, -- ID della directory parente. NULL se il file si trova nel root.
	is_directory BOOLEAN,
	name VARCHAR(255), -- nome del file. VARCHAR: stringa di dimensione variabile
	data BLOB --blob contenente i dati del file. NULL se il file è una directory
	--FOREIGN KEY (parent_id) REFERENCES demoFileSystem(id) --FOREIGN KEY: riferimento alla primary key di un altro field. parent_id è l'id del field a cui ci riferiamo. REFERENCES specifica che questa key si trova nello stesso dfs
	--serve per assicurarsi che l'id del parente esista effettivamente. non so se è proprio necessario
);