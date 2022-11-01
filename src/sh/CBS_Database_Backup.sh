# 
# Shell script that creates a back up of the CBS database.

#


#!/bin/bash

mkdir /home/${USER}/CBS_DATABASE_BACKUP/

mkdir /home/${USER}/CBS_DATABASE_BACKUP/`date +%m_%d_%y`

/usr/bin/mysqldump -u$1 -p$2 -h $3 CBS | gzip > /home/${USER}/CBS_DATABASE_BACKUP/`date +%m_%d_%y`/CBS_`date +%m_%d_%y_%H_%M`.sql.gz